package ru.gorevmichael.sign_v1.domain.usecases

import ru.gorevmichael.files.domain.usecases.SaveJsonUseCase
import ru.gorevmichael.sign_v1.domain.models.SignConfig

class SaveCustomSignConfigUseCase {
    operator fun invoke(name: String, signConfig: SignConfig) {
        val content = """
            {
              "name": "$name",
              "a": "${signConfig.curveConfig.a}",
              "b": "${signConfig.curveConfig.b}",
              "p": "${signConfig.curveConfig.p}",
              "x": "${signConfig.generationPoint.x}",
              "y": "${signConfig.generationPoint.y}",
              "order": "${signConfig.order}"
            }
        """.trimIndent()

        //TODO объединить и вынести пути
        val saveJsonUseCase = SaveJsonUseCase(
            path = "/sign_configs/$name.json",
            content = content
        )
        try {
            saveJsonUseCase()
        } catch (e: Exception) {
            println("Error saving curve config: ${e.message}")
            throw e
        }
    }
}
