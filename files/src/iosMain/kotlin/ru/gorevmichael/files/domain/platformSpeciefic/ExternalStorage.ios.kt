@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package ru.gorevmichael.files.domain.platformSpeciefic

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import platform.Foundation.*
import platform.UIKit.*
import ru.gorevmichael.files.data.File
import kotlinx.cinterop.*
import platform.darwin.NSObject
import kotlin.coroutines.resume

actual object ExternalStorage {

    private val fileManager: NSFileManager = NSFileManager.defaultManager

    actual suspend fun save(
        name: String,
        extension: String,
        content: String
    ): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val tempDirPath = NSTemporaryDirectory()
            val tempDir = NSURL.fileURLWithPath(tempDirPath)
            val tempFileUrl =
                tempDir.URLByAppendingPathComponent("$name.$extension")?.filePathURL!!

            val nsString = NSString.create(content)
            val data = nsString?.dataUsingEncoding(NSUTF8StringEncoding)

            if (data == null) {
                continuation.resume(false)
                return@suspendCancellableCoroutine
            }

            val writeSuccess = data.writeToURL(tempFileUrl, atomically = true)

            if (!writeSuccess) {
                continuation.resume(false)
                return@suspendCancellableCoroutine
            }

            val documentPicker = UIDocumentPickerViewController(
                forExportingURLs = listOf(tempFileUrl),
                asCopy = true
            )

            documentPicker.delegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
                override fun documentPicker(
                    controller: UIDocumentPickerViewController,
                    didPickDocumentsAtURLs: List<*>
                ) {
                    try {
                        fileManager.removeItemAtURL(tempFileUrl, error = null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    continuation.resume(true)
                }

                override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
                    try {
                        fileManager.removeItemAtURL(tempFileUrl, error = null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    continuation.resume(false)
                }
            }

            val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
            rootViewController?.presentViewController(
                documentPicker,
                animated = true,
                completion = null
            )
        }
    }

    actual fun mkdir(path: String): Boolean {
        println("ExternalStorage.mkdir is not directly supported on iOS. Use save() to let user choose folder.")
        return false
    }

    actual fun mkfile(path: String): File? = null

    actual fun load(path: String): File? = null

    actual fun delete(path: String): Boolean = false

    actual fun list(path: String): Flow<List<String>> = flow { emit(emptyList()) }
}