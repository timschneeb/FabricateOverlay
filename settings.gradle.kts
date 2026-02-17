pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
    }
    plugins {
        id("com.android.application") version "9.0.0"
        id("com.android.library") version "9.0.0"
        id("org.jetbrains.kotlin.android") version "2.3.10"
        id("kotlin-parcelize") version "2.3.10"
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "FabricateOverlay"
include(":app", ":fabricateoverlay")
include(":app:hiddenapi")
