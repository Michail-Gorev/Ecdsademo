package ru.gorevmichael.files.domain.platformSpeciefic

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.writeString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.gorevmichael.files.data.File
import java.io.File as JFile

actual object ExternalStorage {
    private var filesDirPath: String = ""
    private fun getFullFile(path: String): JFile {
        val relativePath = path.removePrefix("/")
        return JFile(filesDirPath, relativePath)
    }

    actual fun mkdir(path: String): Boolean {
        val result = getFullFile(path).mkdirs()
        return result
    }

    actual fun mkfile(path: String): File? {
        val jFile = getFullFile(path)
        if (jFile.exists()) return load(path)
        return try {
            jFile.parentFile?.mkdirs()
            if (jFile.createNewFile()) {
                File(
                    path.removePrefix("/"),
                    if (path.endsWith(".json")) File.Type.JSON else File.Type.TEXT,
                    ""
                )
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    actual suspend fun save(
        name: String,
        extension: String,
        content: String
    ): Boolean {
        return try {
            val fileToSave = FileKit.openFileSaver(
                name,
                extension
            )

            fileToSave?.let { file ->
                file.writeString(content)
                true
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    actual fun load(path: String): File? {
        val jFile = getFullFile(path)
        return if (jFile.exists()) {
            File(
                path = path.removePrefix("/"),
                type = if (path.endsWith(".json")) File.Type.JSON else File.Type.TEXT,
                content = jFile.readText()
            )
        } else null
    }

    actual fun delete(path: String): Boolean {
        val result = getFullFile(path).delete()
        return result
    }

    actual fun list(path: String): Flow<List<String>> = flow {
        val dir = getFullFile(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val relativeDir = path.removePrefix("/").let { if (it.isEmpty()) "" else "$it/" }
        val files = dir.listFiles()?.map { relativeDir + it.name } ?: emptyList()
        emit(files)
    }
}