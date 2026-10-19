plugins {
    base
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.ktlint) apply false
}

tasks.register("assembleDebugRelease") {
    dependsOn(":app:assembleDebug", ":app:assembleRelease")
}

tasks.register("cleanBuild") {
    dependsOn("clean", "assembleDebugRelease")
}
