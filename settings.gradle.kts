try {
    val processEnvClass = Class.forName("java.lang.ProcessEnvironment")
    
    // 1. theCaseInsensitiveEnvironment
    try {
        val field = processEnvClass.getDeclaredField("theCaseInsensitiveEnvironment")
        field.isAccessible = true
        (field.get(null) as? MutableMap<*, *>)?.remove("ANDROID_PREFS_ROOT")
    } catch (_: Throwable) {}

    // 2. theEnvironment
    try {
        val field = processEnvClass.getDeclaredField("theEnvironment")
        field.isAccessible = true
        (field.get(null) as? MutableMap<*, *>)?.remove("ANDROID_PREFS_ROOT")
    } catch (_: Throwable) {}

    // 3. theUnmodifiableEnvironment
    try {
        val field = processEnvClass.getDeclaredField("theUnmodifiableEnvironment")
        field.isAccessible = true
        val unmod = field.get(null)
        if (unmod != null) {
            val mField = unmod.javaClass.getDeclaredField("m")
            mField.isAccessible = true
            (mField.get(unmod) as? MutableMap<*, *>)?.remove("ANDROID_PREFS_ROOT")
        }
    } catch (_: Throwable) {}
} catch (_: Throwable) {}

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "InstaConnect"
include(":shared")
include(":androidApp")
include(":desktopApp")
