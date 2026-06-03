package ru.gorevmichael.sign_v2.di

import org.koin.core.module.Module
import ru.gorevmichael.annotations.AutoBuildReference

//TODO
@AutoBuildReference
expect object KoinSign {
    val featureModule: Module
}