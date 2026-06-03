package ru.gorevmichael.sign_v2.di

import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.gorevmichael.annotations.AutoBuildReference

@AutoBuildReference
actual object KoinSign {
    init {
        println("KoinSign instance created on Android") // Должно появиться в консоли
    }
    actual val featureModule: Module = module {
        single<Int> {10}
        single<List<String>>(named("platform_specific_koin_module")) {
            listOf("AndroidSpecific FeatureClass 1 v2", "AndroidSpecific FeatureClass 2 v2")
        }
    }
}