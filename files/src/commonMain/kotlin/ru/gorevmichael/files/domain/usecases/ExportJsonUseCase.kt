package ru.gorevmichael.files.domain.usecases

import ru.gorevmichael.files.domain.platformSpeciefic.ExternalStorage

class ExportJsonUseCase {
    suspend operator fun invoke(
        name: String,
        content: String
    ) {
        if (!ExternalStorage.save(
                name,
                "json",
                content
            )
        ) {
            throw IllegalStateException(
                "File not exported. It's data:" +
                        """
                        name: $name;
                        type: JSON;
                        content: $content
                    """.trimIndent()
            )
        }
    }
}