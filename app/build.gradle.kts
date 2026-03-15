plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ktlint)
}

android {
    namespace = "eu.hxreborn.ghfastpass"
    compileSdk = 36
    buildToolsVersion = "36.0.0"

    defaultConfig {
        applicationId = "eu.hxreborn.ghfastpass"
        minSdk = 29
        targetSdk = 36
        versionCode = project.findProperty("version.code")?.toString()?.toInt() ?: 10000
        versionName = project.findProperty("version.name")?.toString() ?: "1.0.0"
    }

    signingConfigs {
        create("release") {
            fun secret(name: String): String? =
                providers.gradleProperty(name).orElse(providers.environmentVariable(name)).orNull

            val storeFilePath = secret("RELEASE_STORE_FILE")
            if (!storeFilePath.isNullOrBlank()) {
                storeFile = file(storeFilePath)
                storePassword = secret("RELEASE_STORE_PASSWORD")
                keyAlias = secret("RELEASE_KEY_ALIAS")
                keyPassword = secret("RELEASE_KEY_PASSWORD")
                storeType = secret("RELEASE_STORE_TYPE") ?: "PKCS12"
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("release").takeIf { it.storeFile != null }
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    packaging {
        resources {
            pickFirsts += "META-INF/xposed/*"
            excludes += "META-INF/LICENSE*"
        }
    }

    lint {
        abortOnError = true
        checkReleaseBuilds = false
        disable.addAll(
            listOf(
                "PrivateApi",
                "DiscouragedPrivateApi",
                "GradleDependency",
                "AndroidGradlePluginVersion",
            ),
        )
        ignoreTestSources = true
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

kotlin { jvmToolchain(21) }

ktlint {
    version.set("1.8.0")
    android.set(true)
    ignoreFailures.set(false)
}

// AGP 9 built-in Kotlin doesn't register source sets that the ktlint plugin can discover.
// Run ktlint 1.8.0 directly on source files as a workaround.
val ktlintSrc by tasks.registering(JavaExec::class) {
    group = "verification"
    description = "Runs ktlint 1.8.0 on Kotlin source files"
    mainClass.set("com.pinterest.ktlint.Main")
    classpath = configurations.detachedConfiguration(
        dependencies.create("com.pinterest.ktlint:ktlint-cli:1.8.0"),
    )
    args("src/**/*.kt")
}

tasks.named("check").configure {
    dependsOn(ktlintSrc)
}

dependencies {
    compileOnly(libs.libxposed.api)
    implementation(libs.libxposed.service)
}
