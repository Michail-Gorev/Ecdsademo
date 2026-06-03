package ru.gorevmichael.build_outputs.di

import org.koin.core.module.Module
import ru.gorevmichael.annotations.KoinModuleAutoBuild

@KoinModuleAutoBuild
data class FeaturesDI(
    val featureModules: Module
) {
    operator fun invoke(): Module = featureModules
}

