@file:OptIn(KspExperimental::class)

import com.google.devtools.ksp.KspExperimental

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    id("com.google.devtools.ksp") version "2.3.7"
}
val includedFeatures = (properties["includedFeatures"] as? String)?.split(",") ?: emptyList()

kotlin {

    androidLibrary {
        namespace = "ru.gorevmichael.build_outputs"
        compileSdk {
            version = release(36) { minorApiLevel = 1 }
        }
        minSdk = 31

        withHostTestBuilder {}

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    val xcfName = "build_outputsKit"

    iosX64 { binaries.framework { baseName = xcfName } }
    iosArm64 { binaries.framework { baseName = xcfName } }
    iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

    sourceSets {
        androidMain {
//            kotlin.setSrcDirs(emptyList<String>())

            if (includedFeatures.contains("sign_v2")) {
                kotlin.srcDir("src/signv2/androidMain/kotlin/ru/gorevmichael/build_outputs/di")
            } else {
                kotlin.srcDir("src/signv1/androidMain/kotlin/ru/gorevmichael/build_outputs/di")
            }
        }

        commonMain {
//            kotlin.setSrcDirs(emptyList<String>())

            if (includedFeatures.contains("sign_v2")) {
                kotlin.srcDir("src/signv2/commonMain/kotlin/ru/gorevmichael/build_outputs/di")
            } else {
                kotlin.srcDir("src/signv1/commonMain/kotlin/ru/gorevmichael/build_outputs/di")
            }

            dependencies {
                includedFeatures.forEach { feature ->
                    api(project(":$feature"))
                }
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.testExt.junit)
            }
        }

        iosMain {
//            kotlin.setSrcDirs(emptyList<String>())

            if (includedFeatures.contains("sign_v2")) {
                kotlin.srcDir("src/signv2/iosMain/kotlin/ru/gorevmichael/build_outputs/di")
            } else {
                kotlin.srcDir("src/signv1/iosMain/kotlin/ru/gorevmichael/build_outputs/di")
            }
            dependencies {}
        }
    }
}
