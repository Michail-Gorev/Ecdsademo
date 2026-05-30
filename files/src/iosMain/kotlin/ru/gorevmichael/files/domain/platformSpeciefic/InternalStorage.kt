package ru.gorevmichael.files.domain.platformSpeciefic

import ru.gorevmichael.files.data.File
import platform.Foundation.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

@OptIn(ExperimentalForeignApi::class)
actual object InternalStorage {
    private var filesDirPath: String = ""
    private val refreshSignal = MutableSharedFlow<Unit>(replay = 1)

    actual fun initialize(filesDirPath: String) {
        this.filesDirPath = if (filesDirPath.endsWith("/")) filesDirPath else "$filesDirPath/"
        refreshSignal.tryEmit(Unit)
    }

    actual fun mkdir(path: String): Boolean {
        val fullPath = filesDirPath + path.removePrefix("/")
        val result = NSFileManager.defaultManager.createDirectoryAtPath(fullPath, true, null, null)
        if (result) refreshSignal.tryEmit(Unit)
        return result
    }

    actual fun mkfile(path: String): File? {
        val fullPath = filesDirPath + path.removePrefix("/")
        val fileManager = NSFileManager.defaultManager
        if (fileManager.fileExistsAtPath(fullPath)) return load(path)
        
        val success = fileManager.createFileAtPath(fullPath, null, null)
        return if (success) {
            refreshSignal.tryEmit(Unit)
            File(path.removePrefix("/"), if (path.endsWith(".json")) File.Type.JSON else File.Type.TEXT, "")
        } else null
    }

    actual fun save(file: File): Boolean {
        val fullPath = filesDirPath + file.path.removePrefix("/")
        val nsString = file.content as NSString
        
        val directory = (fullPath as NSString).stringByDeletingLastPathComponent
        NSFileManager.defaultManager.createDirectoryAtPath(directory, true, null, null)
        
        val result = nsString.writeToFile(fullPath, true, NSUTF8StringEncoding, null)
        if (result) refreshSignal.tryEmit(Unit)
        return result
    }

    actual fun load(path: String): File? {
        val fullPath = filesDirPath + path.removePrefix("/")
        val content = NSString.stringWithContentsOfFile(fullPath, NSUTF8StringEncoding, null)
        return if (content != null) {
            File(
                path = path.removePrefix("/"),
                type = if (path.endsWith(".json")) File.Type.JSON else File.Type.TEXT,
                content = content
            )
        } else null
    }

    actual fun delete(path: String): Boolean {
        val fullPath = filesDirPath + path.removePrefix("/")
        val result = NSFileManager.defaultManager.removeItemAtPath(fullPath, null)
        if (result) refreshSignal.tryEmit(Unit)
        return result
    }

    actual fun list(path: String): Flow<List<String>> = flow {
        refreshSignal.onStart { emit(Unit) }.collect {
            val fullPath = filesDirPath + path.removePrefix("/")
            val contents = NSFileManager.defaultManager.contentsOfDirectoryAtPath(fullPath, null) as? List<String>
            emit(contents?.map { "$path/$it" } ?: emptyList())
        }
    }
}
