package ru.gorevmichael.files.domain.platformSpeciefic

import kotlinx.coroutines.flow.Flow
import ru.gorevmichael.files.data.File

/**
 * Объект для работы с "внутренним" хранилищем
 */
expect object InternalStorage {
    fun initialize(filesDirPath: String)
    fun mkdir(path: String): Boolean
    fun mkfile(path: String): File?
    fun save(file: File): Boolean
    fun load(path: String): File?
    fun delete(path: String): Boolean
    fun list(path: String): Flow<List<String>>
}
