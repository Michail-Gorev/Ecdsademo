package ru.gorevmichael.files.domain.usecases

import ru.gorevmichael.files.data.File
import ru.gorevmichael.files.domain.platformSpeciefic.InternalStorage

class SaveJsonUseCase(
    val path: String,
    val content: String
) {
    operator fun invoke() {
        val file = File(path, File.Type.JSON, content)
        InternalStorage.mkfile(path)
        if (!InternalStorage.save(file)) {
            throw IllegalStateException(
                "File not saved. It's data:" +
                        """
                        path: ${file.path};
                        type: ${file.type};
                        content: ${file.content}
                    """.trimIndent()
            )
        }
    }
}