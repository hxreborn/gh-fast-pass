package eu.hxreborn.ghfastpass.xposed

import android.util.Log
import eu.hxreborn.ghfastpass.BuildConfig
import eu.hxreborn.ghfastpass.xposed.hook.TwoFactorHook
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageReadyParam

@PublishedApi
internal lateinit var module: GitHubFastPassModule
    private set

class GitHubFastPassModule : XposedModule() {
    override fun onModuleLoaded(param: ModuleLoadedParam) {
        module = this
        log(Log.INFO, TAG, "loaded module version=${BuildConfig.VERSION_NAME}")
    }

    override fun onPackageReady(param: PackageReadyParam) {
        if (param.packageName != GITHUB_PACKAGE || !param.isFirstPackage) return

        runCatching {
            TwoFactorHook.hook(param.classLoader)
        }.onSuccess {
            log(Log.INFO, TAG, "registered hooks pkg=$GITHUB_PACKAGE")
        }.onFailure {
            log(Log.ERROR, TAG, "failed hook-registration pkg=$GITHUB_PACKAGE", it)
        }
    }

    private companion object {
        const val TAG = "GHFastPass"
        const val GITHUB_PACKAGE = "com.github.android"
    }
}
