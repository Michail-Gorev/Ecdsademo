package ru.gorevmichael.sign_v2.di

import org.koin.core.module.Module
import ru.gorevmichael.annotations.AutoBuildReference

//TODO
@AutoBuildReference
expect class sign_v2KoinModule() {
    val featureModule: Module
}