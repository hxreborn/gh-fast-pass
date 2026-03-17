package eu.hxreborn.ghfastpass.xposed.hook

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import io.github.libxposed.api.XposedModule
import java.lang.ref.WeakReference
import java.lang.reflect.Method
import java.lang.reflect.Modifier

object TwoFactorHooker {
    private const val TAG = "GHFastPass"
    private const val ACTIVITY = "com.github.android.twofactor.TwoFactorActivity"
    private const val DIALOG = "com.github.android.twofactor.TwoFactorDialog"
    private const val TARGET_STATE = "FINISHED_APPROVED"

    @Volatile
    private var pendingActivity: WeakReference<Activity>? = null

    fun hook(
        module: XposedModule,
        classLoader: ClassLoader,
    ) {
        val dialogClass = classLoader.loadClass(DIALOG)
        val activityClass = classLoader.loadClass(ACTIVITY)

        // Enum class name is obfuscated but FINISHED_APPROVED survives in metadata
        val stateEnum =
            dialogClass.findFinishedApprovedEnum()
                ?: return module.log(Log.WARN, TAG, "$DIALOG state enum not found")

        // State mapper name is obfuscated so match by signature instead
        val stateMapper =
            dialogClass.findStateMapper(stateEnum)
                ?: return module.log(Log.WARN, TAG, "State mapper not found on $DIALOG")

        val onCreate = activityClass.getDeclaredMethod("onCreate", Bundle::class.java)
        module.hook(onCreate).intercept { chain ->
            chain.proceed()
            pendingActivity = WeakReference(chain.thisObject as Activity)
        }

        module.hook(stateMapper).intercept { chain ->
            val result = chain.proceed()
            val state = result as? Enum<*> ?: return@intercept result
            if (state.name != TARGET_STATE) return@intercept result

            val activity =
                pendingActivity?.get()?.takeUnless { it.isFinishing } ?: return@intercept result
            pendingActivity = null

            module.log(Log.INFO, TAG, "Auto-dismissing verification dialog")

            // Post to avoid side effects during Compose composition
            Handler(Looper.getMainLooper()).post { activity.finish() }
            result
        }
    }

    private fun Class<*>.findFinishedApprovedEnum(): Class<*>? =
        declaredClasses.firstOrNull { cls ->
            cls.isEnum && cls.enumConstants.orEmpty().any { (it as Enum<*>).name == TARGET_STATE }
        }

    private fun Class<*>.findStateMapper(enumType: Class<*>): Method? =
        declaredMethods.firstOrNull { method ->
            Modifier.isStatic(method.modifiers) && method.parameterCount == 1 &&
                method.returnType == enumType
        }
}
