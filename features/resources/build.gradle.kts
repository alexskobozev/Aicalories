import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_11)
                }
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.components.resources)
            implementation(libs.compose.runtime)
        }
    }
    jvmToolchain(11)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.wishnewjam.aicalories.resources"
    generateResClass = auto
}

android {
    namespace = "com.wishnewjam.aicalories.resources"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
}