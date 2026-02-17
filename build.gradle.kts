plugins {
    id("com.android.application") version "9.0.0" apply false
    id("com.android.library") version "9.0.0" apply false
    id("org.jetbrains.kotlin.plugin.parcelize") version "2.3.10" apply false
}

// Register `clean` task
tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
