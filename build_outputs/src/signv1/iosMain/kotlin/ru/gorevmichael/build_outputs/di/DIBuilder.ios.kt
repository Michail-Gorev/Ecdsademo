package ru.gorevmichael.build_outputs.di

import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single<List<String>>(named("platform_specific")) {
            listOf("Platform Specific Feature1v1 for iOS, Platform Specific Feature2v1 for iOS")
        }
    }