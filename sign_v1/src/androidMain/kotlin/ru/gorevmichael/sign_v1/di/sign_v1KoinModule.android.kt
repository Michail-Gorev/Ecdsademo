package ru.gorevmichael.sign_v1.di

import org.koin.core.module.Module
import org.koin.dsl.module
import ru.gorevmichael.annotations.AutoBuildReference
import ru.gorevmichael.core.interfaces.DummyInterface
import ru.gorevmichael.sign_v1.data.custom.DummyInterfaceImpl

@AutoBuildReference
actual class sign_v1KoinModule actual constructor(){
    actual val featureModule: Module = module {
        single<DummyInterface> {
            DummyInterfaceImpl("Android", 10)
        }
    }
}