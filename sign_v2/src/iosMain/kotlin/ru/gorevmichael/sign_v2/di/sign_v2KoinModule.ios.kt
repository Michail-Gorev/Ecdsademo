package ru.gorevmichael.sign_v2.di

import org.koin.core.module.Module
import org.koin.dsl.module
import ru.gorevmichael.annotations.AutoBuildReference
import ru.gorevmichael.core.interfaces.DummyInterface
import ru.gorevmichael.sign_v2.data.custom.DummyInterfaceImpl

@AutoBuildReference
actual class sign_v2KoinModule actual constructor() {
    actual val featureModule: Module = module {
        single<DummyInterface> {
            DummyInterfaceImpl("iOS")
        }
    }
}