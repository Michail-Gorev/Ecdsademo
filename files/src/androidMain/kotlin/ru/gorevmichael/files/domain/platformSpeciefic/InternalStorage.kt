package ru.gorevmichael.files.domain.platformSpeciefic

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import ru.gorevmichael.files.data.File
import java.io.File as JFile

actual object InternalStorage {
    private var filesDirPath: String = ""
    private val refreshSignal = MutableSharedFlow<Unit>(replay = 1)

    actual fun initialize(filesDirPath: String) {
        this.filesDirPath = if (filesDirPath.endsWith(JFile.separator)) filesDirPath else filesDirPath + JFile.separator
        refreshSignal.tryEmit(Unit)
    }

    private fun getFullFile(path: String): JFile {
        val relativePath = path.removePrefix("/")
        return JFile(filesDirPath, relativePath)
    }

    actual fun mkdir(path: String): Boolean {
        val result = getFullFile(path).mkdirs()
        if (result) refreshSignal.tryEmit(Unit)
        return result
    }

    actual fun mkfile(path: String): File? {
        val jFile = getFullFile(path)
        if (jFile.exists()) return load(path)
        return try {
            jFile.parentFile?.mkdirs()
            if (jFile.createNewFile()) {
                refreshSignal.tryEmit(Unit)
                File(path.removePrefix("/"), if (path.endsWith(".json")) File.Type.JSON else File.Type.TEXT, "")
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    actual fun save(file: File): Boolean {
        return try {
            val jFile = getFullFile(file.path)
            jFile.parentFile?.mkdirs()
            jFile.writeText(file.content)
            refreshSignal.tryEmit(Unit)
            true
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
        if (result) refreshSignal.tryEmit(Unit)
        return result
    }

    actual fun list(path: String): Flow<List<String>> = flow {
        refreshSignal.onStart { emit(Unit) }.collect {
            val dir = getFullFile(path)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val relativeDir = path.removePrefix("/").let { if (it.isEmpty()) "" else "$it/" }
            val files = dir.listFiles()?.map { relativeDir + it.name } ?: emptyList()
            emit(files)
        }
    }
}
