package ru.gorevmichael.files.domain.usecases

import ru.gorevmichael.files.domain.platformSpeciefic.InternalStorage

class LoadJsonUseCase(
    val path: String
) {
    operator fun invoke(): String {
        val file = InternalStorage.load(path)
            ?: throw NullPointerException("File not found by path: $path")
        return file.content
    }
}