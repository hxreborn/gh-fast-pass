package eu.hxreborn.ghfastpass.xposed

import eu.hxreborn.ghfastpass.BuildConfig
import eu.hxreborn.ghfastpass.xposed.hook.TwoFactorHooker
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam

class GitHubFastPassModule(
    base: XposedInterface,
    param: ModuleLoadedParam,
) : XposedModule(base, param) {

    init {
        log("GH FastPass v${BuildConfig.VERSION_NAME} loaded")
    }

    override fun onPackageLoaded(param: PackageLoadedParam) {
        if (param.packageName != GITHUB_PACKAGE || !param.isFirstPackage) return
        runCatching { TwoFactorHooker.hook(this, param.classLoader) }
            .onSuccess { log("Hooks registered for $GITHUB_PACKAGE") }
            .onFailure { log("Hook registration failed", it) }
    }

    companion object {
        const val GITHUB_PACKAGE = "com.github.android"
    }
}
