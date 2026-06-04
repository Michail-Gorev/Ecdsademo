import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    androidLibrary {
        namespace = "ru.gorevmichael.ecdsademo.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project.dependencies.platform("io.insert-koin:koin-bom:3.5.6"))
            implementation("io.insert-koin:koin-core")
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation("io.insert-koin:koin-compose:4.0.0")
            implementation(libs.compose.components.resources)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(project(":core"))
            implementation(project(":annotations"))
            implementation(project(":math"))
            implementation(project(":files"))
            implementation(project(":build_outputs"))
            implementation(libs.bignum)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
val includedFeatures = (properties["includedFeatures"] as String).split(",")
dependencies {
    includedFeatures.forEach { feature ->
        add("commonMainImplementation", project(":$feature"))
    }
}

val generateFeaturesLoader by tasks.registering {
    val outputDir = file("$buildDir/generated/featuresLoader/commonMain/")
    val packageDir = outputDir.resolve("ru/gorevmichael/ecdsademo/di")
    packageDir.mkdirs()
    val featureImports = includedFeatures.joinToString("\n") { feature ->
        "import ru.gorevmichael.$feature.di.${feature}KoinModule"
    }
    val featureModules = includedFeatures.joinToString(",\n    ") { feature ->
        "${feature}KoinModule().featureModule"
    }
    doLast {
        outputDir.mkdirs()
        val actualFile = packageDir.resolve("KoinUtils.kt")
        actualFile.writeText(
            """
//Auto-generated file for koin modules
//Do not edit it manually!
//@gorevmichael
package ru.gorevmichael.ecdsademo.di
                    
import org.koin.core.module.Module
$featureImports
                    
fun loadFeatureModules(): List<Module> = listOf(
    $featureModules
)
            """.trimIndent()
        )
    }
}

kotlin.sourceSets["commonMain"].kotlin.srcDir(
    "$buildDir/generated/featuresLoader/commonMain"
)
kotlin.sourceSets.forEach { sourceSet ->
    sourceSet.kotlin.srcDir("$buildDir/generated/featuresLoader/${sourceSet.name}")
}

tasks.named("compileKotlinMetadata") {
    dependsOn(generateFeaturesLoader)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn(generateFeaturesLoader)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile>().configureEach {
    dependsOn(generateFeaturesLoader)
}