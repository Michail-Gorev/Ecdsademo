package ru.gorevmichael.sign_v2.di

import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module
import ru.gorevmichael.annotations.AutoBuildReference

@AutoBuildReference
actual class KoinSignV2 {
    actual val featureModule: Module
        get() = module {
            single<List<String>>(named("platform_specific_koin_module")) {
                listOf("iOSSpecific FeatureClass 1 v2", "iOSSpecific FeatureClass 2 v2")
            }
        }
}