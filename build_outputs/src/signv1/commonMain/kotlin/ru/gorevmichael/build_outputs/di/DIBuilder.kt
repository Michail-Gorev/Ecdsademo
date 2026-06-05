package ru.gorevmichael.build_outputs.di

import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.gorevmichael.sign_v1.di.featureModule

val commonModule: Module = module {
    single<String>(named("common_feature")) {
        "!!!Common feature!!!"
    }
}

expect val platformModule: Module

val koinModules = module {
    includes(commonModule, featureModule, platformModule)
}