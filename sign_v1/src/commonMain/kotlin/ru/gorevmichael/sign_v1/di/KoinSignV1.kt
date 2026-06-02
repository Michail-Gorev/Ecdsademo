package ru.gorevmichael.sign_v1.di

import org.koin.core.module.Module
import ru.gorevmichael.annotations.AutoBuildReference

@AutoBuildReference
expect class KoinSignV1 {
    val featureModule: Module
}