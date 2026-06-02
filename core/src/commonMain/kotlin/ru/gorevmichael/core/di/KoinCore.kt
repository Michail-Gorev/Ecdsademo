package ru.gorevmichael.core.di

import org.koin.core.module.Module
import ru.gorevmichael.annotations.AutoBuild

@AutoBuild
data class FeaturesDI(
    val featureModules: List<Module> = emptyList()
) {
    operator fun invoke(): List<Module> = featureModules
    fun test() {
//        FeaturesDIBuilder()()
    }
}
//TODO этот класс должен быть автогенерируемым!


