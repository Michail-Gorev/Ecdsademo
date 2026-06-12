@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package ru.gorevmichael.files.domain.platformSpeciefic

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import platform.Foundation.*
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import ru.gorevmichael.files.data.File
import kotlinx.cinterop.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onStart

actual object InternalStorage {

    private val fileManager: NSFileManager = NSFileManager.defaultManager
    private val refreshSignal = MutableSharedFlow<Unit>(replay = 1)

    private val documentsDirectoryURL: NSURL by lazy {
        fileManager.URLsForDirectory(
            NSDocumentDirectory,
            inDomains = NSUserDomainMask
        )[0] as NSURL
    }

    actual fun initialize(filesDirPath: String) {
        println("Warning: InternalStorage's initialize() is ignored on iOS. Using Documents directory: $documentsDirectoryURL")
    }

    private fun getFullURL(relativePath: String): NSURL {
        val cleanPath = relativePath.removePrefix("/")
        val fullPath = documentsDirectoryURL.path?.let { path ->
            if (path.endsWith("/")) path + cleanPath else "$path/$cleanPath"
        } ?: error("Failed to get documents directory path")

        return NSURL.fileURLWithPath(fullPath)
    }

    actual fun mkdir(path: String): Boolean {
        val url = getFullURL(path)

        return memScoped {
            val errorPtr = alloc<ObjCObjectVar<NSError?>>()
            errorPtr.value = null

            val success = fileManager.createDirectoryAtURL(
                url = url,
                withIntermediateDirectories = true,
                attributes = null,
                error = errorPtr.ptr
            )

            if (!success) {
                val localizedError = errorPtr.value?.localizedDescription
                throw Exception("Failed to create directory '$path': ${localizedError ?: "Unknown error"}")
            }

            success
        }
    }

    actual fun mkfile(path: String): File? {
        val url = getFullURL(path)

        return memScoped {
            if (fileManager.fileExistsAtPath(url.path ?: return@memScoped null)) {
                return@memScoped load(path)
            }

            val parentUrl = url.URLByDeletingLastPathComponent
            if (parentUrl?.path != null && !fileManager.fileExistsAtPath(parentUrl.path!!)) {
                val errorPtr = alloc<ObjCObjectVar<NSError?>>()
                errorPtr.value = null

                val mkdirSuccess = fileManager.createDirectoryAtURL(
                    url = parentUrl,
                    withIntermediateDirectories = true,
                    attributes = null,
                    error = errorPtr.ptr
                )

                if (!mkdirSuccess) {
                    val localizedError = errorPtr.value?.localizedDescription
                    throw Exception("Failed to create parent directories for '$path': ${localizedError ?: "Unknown error"}")
                }
            }

            val success = fileManager.createFileAtPath(
                path = url.path ?: return@memScoped null,
                contents = null,
                attributes = null
            )

            if (success) {
                File(
                    path = path.removePrefix("/"),
                    type = if (path.endsWith(".json")) File.Type.JSON else File.Type.TEXT,
                    content = ""
                )
            } else {
                throw Exception("Failed to create file '$path'")
            }
        }
    }

    actual fun save(file: File): Boolean {
        val url = getFullURL(file.path)

        return memScoped {
            val parentUrl = url.URLByDeletingLastPathComponent
            if (parentUrl?.path != null && !fileManager.fileExistsAtPath(parentUrl.path!!)) {
                val errorPtr = alloc<ObjCObjectVar<NSError?>>()
                errorPtr.value = null

                fileManager.createDirectoryAtURL(
                    url = parentUrl,
                    withIntermediateDirectories = true,
                    attributes = null,
                    error = errorPtr.ptr
                )
            }

            val nsString = NSString.create(file.content)
            val data = nsString?.dataUsingEncoding(NSUTF8StringEncoding)
                ?: throw Exception("Failed to encode file content to UTF-8")

            val success = data.writeToURL(
                url = url,
                atomically = true
            )

            if (!success) {
                throw Exception("Failed to save file '${file.path}'")
            }

            success
        }
    }

    actual fun load(path: String): File? {
        val url = getFullURL(path)

        if (!fileManager.fileExistsAtPath(url.path ?: return null)) {
            return null
        }

        return memScoped {
            val errorPtr = alloc<ObjCObjectVar<NSError?>>()
            errorPtr.value = null

            val data = NSString.stringWithContentsOfURL(
                url = url,
                encoding = NSUTF8StringEncoding,
                error = errorPtr.ptr
            )

            if (data == null) {
                val localizedError = errorPtr.value?.localizedDescription
                throw Exception("Failed to load file '$path': ${localizedError ?: "Unknown error"}")
            }

            File(
                path = path.removePrefix("/"),
                type = if (path.endsWith(".json")) File.Type.JSON else File.Type.TEXT,
                content = data
            )
        }
    }

    actual fun delete(path: String): Boolean {
        val url = getFullURL(path)

        return memScoped {
            val errorPtr = alloc<ObjCObjectVar<NSError?>>()
            errorPtr.value = null

            val success = fileManager.removeItemAtURL(
                URL = url,
                error = errorPtr.ptr
            )

            if (!success) {
                val localizedError = errorPtr.value?.localizedDescription
                throw Exception("Failed to delete '$path': ${localizedError ?: "Unknown error"}")
            }

            success
        }
    }

    actual fun list(path: String): Flow<List<String>> = flow {
        refreshSignal.onStart { emit(Unit) }.collect {
            val url = getFullURL(path)

            if (!fileManager.fileExistsAtPath(url.path!!)) {
                mkdir(path)
            }

            memScoped {
                val errorPtr = alloc<ObjCObjectVar<NSError?>>()
                errorPtr.value = null

                val contents = fileManager.contentsOfDirectoryAtURL(
                    url = url,
                    includingPropertiesForKeys = null,
                    options = NSDirectoryEnumerationSkipsHiddenFiles,
                    error = errorPtr.ptr
                ) as? List<NSURL>

                if (contents == null) {
                    val localizedError = errorPtr.value?.localizedDescription
                    throw Exception("Failed to list directory '$path': ${localizedError ?: "Unknown error"}")
                }

                val relativeDir = path.removePrefix("/").let {
                    if (it.isEmpty()) "" else "$it/"
                }

                val filePaths = contents.mapNotNull { nsUrl ->
                    nsUrl.lastPathComponent?.let { fileName ->
                        relativeDir + fileName
                    }
                }

                emit(filePaths)
            }
        }
    }
}