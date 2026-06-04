@file:OptIn(KspExperimental::class)

import com.google.devtools.ksp.KspExperimental

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    id("com.google.devtools.ksp") version "2.3.7"
}

kotlin {

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "ru.gorevmichael.sign_v2"
        compileSdk {
            version = release(36) { minorApiLevel = 1 }
        }
        minSdk = 31

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    // For iOS targets, this is also where you should
    // configure native binary output. For more information, see:
    // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

    // A step-by-step guide on how to include this library in an XCode
    // project can be found here:
    // https://developer.android.com/kotlin/multiplatform/migrate
    val xcfName = "sign_v2Kit"

    iosX64 {
        binaries.framework {
            baseName = xcfName
            isStatic = true
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
            isStatic = true
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
            isStatic = true
        }
    }

    // Source set declarations.
    // Declaring a target automatically creates a source set with the same name. By default, the
    // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
    // common to share sources between related targets.
    // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
    sourceSets {
        commonMain {
            dependencies {
                implementation(project.dependencies.platform("io.insert-koin:koin-bom:3.5.6"))
                implementation(libs.kotlin.stdlib)
                implementation("dev.whyoleg.cryptography:cryptography-core:0.5.0")
                implementation("dev.whyoleg.cryptography:cryptography-provider-optimal:0.5.0")
                implementation(libs.bignum)
                implementation("io.insert-koin:koin-core")
                implementation("io.insert-koin:koin-compose:4.0.0")
                implementation(project(":core"))
                implementation(project(":math"))
                implementation(project(":files"))
                implementation(project(":annotations"))
                // Add KMP dependencies here
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                // Add Android-specific dependencies here. Note that this source set depends on
                // commonMain by default and will correctly pull the Android artifacts of any KMP
                // dependencies declared in commonMain.
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
            dependencies {
                // Add iOS-specific dependencies here. This a source set created by Kotlin Gradle
                // Plugin (KGP) that each specific iOS target (e.g., iosX64) depends on as
                // part of KMP’s default source set hierarchy. Note that this source set depends
                // on common by default and will correctly pull the iOS artifacts of any
                // KMP dependencies declared in commonMain.
            }
        }
    }

}

//dependencies {
//    add("kspCommonMainMetadata", project(":build_processor"))
//}
//ksp {
//    useKsp2 = true
//    arg("includedFeatures", properties["includedFeatures"] as String)
//}