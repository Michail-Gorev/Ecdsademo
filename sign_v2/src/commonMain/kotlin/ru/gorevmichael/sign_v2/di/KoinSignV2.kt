package ru.gorevmichael.sign_v2.di

import org.koin.core.module.Module
import ru.gorevmichael.annotations.AutoBuildReference

@AutoBuildReference
expect class KoinSignV2 {
    val featureModule: Module
}