import dev.detekt.gradle.Detekt
import dev.detekt.gradle.extensions.DetektExtension
import org.gradle.kotlin.dsl.withType

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidLint) apply false
    alias(libs.plugins.detectLint) apply false
}

subprojects {
    afterEvaluate {
        if (plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") ||
            plugins.hasPlugin("com.android.application") ||
            plugins.hasPlugin("com.android.library")) {

            plugins.apply("dev.detekt")

            extensions.configure<DetektExtension> {
                val srcDirs = when {
                    plugins.hasPlugin("com.android.application") -> listOf("src/main/kotlin")
                    plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") -> listOf(
                        "src/commonMain/kotlin",
                        "src/androidMain/kotlin",
                        "src/iosMain/kotlin"
                    )
                    else -> listOf("src/main/kotlin")
                }

                source.setFrom(srcDirs.map { file(it) })
                reportsDir.set(file("$rootDir/config/reports"))
                parallel = true
                buildUponDefaultConfig = true
                config.setFrom(files("$rootDir/config/detekt.yml"))
                autoCorrect = false
            }

        }
    }
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true) // observe findings in your browser with structure and code snippets
//        checkstyle.required.set(true) // checkstyle(xml) like format mainly for integrations like Jenkins
        sarif.required.set(true) // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with GitHub Code Scanning
//        markdown.required.set(true) // simple Markdown format
    }
}