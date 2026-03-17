package eu.hxreborn.ghfastpass.xposed

import android.util.Log
import eu.hxreborn.ghfastpass.BuildConfig
import eu.hxreborn.ghfastpass.xposed.hook.TwoFactorHooker
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageReadyParam

class GitHubFastPassModule : XposedModule() {
    override fun onModuleLoaded(param: ModuleLoadedParam) {
        log(Log.INFO, TAG, "GH FastPass v${BuildConfig.VERSION_NAME} loaded")
    }

    override fun onPackageReady(param: PackageReadyParam) {
        if (param.packageName != GITHUB_PACKAGE || !param.isFirstPackage) return

        runCatching {
            TwoFactorHooker.hook(this, param.classLoader)
        }.onSuccess {
            log(Log.INFO, TAG, "Hooks registered for $GITHUB_PACKAGE")
        }.onFailure {
            log(Log.ERROR, TAG, "Hook registration failed", it)
        }
    }

    private companion object {
        const val TAG = "GHFastPass"
        const val GITHUB_PACKAGE = "com.github.android"
    }
}
