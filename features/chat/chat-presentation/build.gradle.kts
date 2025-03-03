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
            implementation(project(":features:chat:chat-domain"))
            implementation(project(":features:resources"))
            implementation(libs.components.resources)
            implementation(libs.compose.runtime)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.jetbrains.compose.material3)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }
    }
    jvmToolchain(11)
}

android {
    namespace = "com.wishnewjam.aicalories.chat.presentation"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
}
