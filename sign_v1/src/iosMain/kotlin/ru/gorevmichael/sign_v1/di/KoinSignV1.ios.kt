package ru.gorevmichael.sign_v1.di

import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.gorevmichael.annotations.AutoBuildReference

@AutoBuildReference
actual class KoinSignV1 {
    actual val featureModule: Module
        get() = module {
            single<List<String>>(named("platform_specific_koin_module")) {
                listOf("AndroidSpecific FeatureClass 1 v1", "AndroidSpecific FeatureClass 2 v1")
            }
        }
}