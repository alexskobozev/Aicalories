enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Aicalories"
include(":androidApp")
include(":shared")

include(":features:resources")
include(":features:network")
include(":features:chat:chat-domain")
include(":features:chat:chat-data")
include(":features:chat:chat-presentation")