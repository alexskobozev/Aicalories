import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
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
            implementation(project(":features:chat:chat-domain"))
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.jetbrains.compose.material3)
        }
    }
    jvmToolchain(11)
}

android {
    namespace = "com.wishnewjam.aicalories.chat.domain"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
}
