package ru.gorevmichael.files.domain.platformSpeciefic

import kotlinx.coroutines.flow.Flow
import ru.gorevmichael.files.data.File

/**
 * Объект для работы с "внешним" хранилищем
 */
expect object ExternalStorage {
    fun mkdir(path: String): Boolean
    fun mkfile(path: String): File?
    suspend fun save(
        name: String,
        extension: String,
        content: String
    ): Boolean

    fun load(path: String): File?
    fun delete(path: String): Boolean
    fun list(path: String): Flow<List<String>>
}