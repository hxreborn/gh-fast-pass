package eu.hxreborn.ghfastpass.xposed.hook

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedInterface.AfterHookCallback
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.annotations.AfterInvocation
import io.github.libxposed.api.annotations.XposedHooker
import java.lang.ref.WeakReference
import java.lang.reflect.Method
import java.lang.reflect.Modifier

object TwoFactorHooker {

    private const val TWO_FACTOR_ACTIVITY =
        "com.github.android.twofactor.TwoFactorActivity"
    private const val TWO_FACTOR_DIALOG =
        "com.github.android.twofactor.TwoFactorDialog"
    private const val TARGET_STATE = "FINISHED_APPROVED"

    @Volatile
    private var pendingActivity: WeakReference<Activity>? = null

    private lateinit var module: XposedModule

    fun hook(module: XposedModule, classLoader: ClassLoader) {
        this.module = module
        val dialogClass = classLoader.loadClass(TWO_FACTOR_DIALOG)
        val activityClass = classLoader.loadClass(TWO_FACTOR_ACTIVITY)

        val stateEnum = dialogClass.findFinishedApprovedEnum()
            ?: return module.log("$TWO_FACTOR_DIALOG state enum not found")

        val stateMapper = dialogClass.findStateMapper(stateEnum)
            ?: return module.log("State mapper method not found on $TWO_FACTOR_DIALOG")

        module.hook(
            activityClass.getDeclaredMethod("onCreate", Bundle::class.java),
            CaptureActivityHooker::class.java,
        )
        module.hook(stateMapper, AutoDismissHooker::class.java)
    }

    private fun Class<*>.findFinishedApprovedEnum(): Class<*>? =
        declaredClasses.firstOrNull { cls ->
            cls.isEnum &&
                cls.enumConstants.orEmpty().any {
                    (it as Enum<*>).name == TARGET_STATE
                }
        }

    private fun Class<*>.findStateMapper(enumType: Class<*>): Method? =
        declaredMethods.firstOrNull { m ->
            Modifier.isStatic(m.modifiers) &&
                m.parameterCount == 1 &&
                m.returnType == enumType
        }

    @XposedHooker
    class CaptureActivityHooker : XposedInterface.Hooker {
        companion object {
            @JvmStatic
            @AfterInvocation
            fun after(callback: AfterHookCallback) {
                pendingActivity = WeakReference(callback.thisObject as Activity)
            }
        }
    }

    @XposedHooker
    class AutoDismissHooker : XposedInterface.Hooker {
        companion object {
            @JvmStatic
            @AfterInvocation
            fun after(callback: AfterHookCallback) {
                val state = callback.result as? Enum<*> ?: return
                if (state.name != TARGET_STATE) return

                val activity = pendingActivity?.get()
                    ?.takeUnless { it.isFinishing } ?: return
                pendingActivity = null
                module.log("Auto-dismissing verification dialog")
                Handler(Looper.getMainLooper()).post { activity.finish() }
            }
        }
    }
}
