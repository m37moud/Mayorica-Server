package com.example.service.storage

import io.ktor.http.content.*
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

/**
 * Storage Service to manage our files
 * @property myConfig AppConfig Configuration of our service
 */
class StorageServiceImpl(
    private val myConfig: HoconApplicationConfig
) : StorageService {
    private val uploadDir by lazy {
        myConfig.propertyOrNull("upload.dir")?.getString() ?: "uploads"
    }

    init {
        logger.debug { " Starting Storage Service in $uploadDir" }
        initStorageDirectory()
    }

    /**
     * Inits the storage directory
     * If not exists, creates it
     * If exists, clean it if dev
     */
    private fun initStorageDirectory() {
        // Create upload directory if not exists (or ignore if exists)
        // and clean if dev
        Files.createDirectories(Path.of(uploadDir))
        if (myConfig.propertyOrNull("ktor.environment")?.getString() == "dev") {
            logger.debug { "Cleaning storage directory in $uploadDir" }
            File(uploadDir).listFiles()?.forEach { it.delete() }
        }
    }

    /**
     * Saves a file in our storage
     * @param fileName String Name of the file
     * @param fileUrl String URL of the file
     * @param fileBytes ByteArray Bytes of the file
     * @return Result<Map<String, String>, StorageError> Map if Ok, StorageError if not
     */
    override suspend fun saveFile(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteArray
    ): Boolean {
        logger.debug { "Saving file in: $fileName" }

        return withContext(Dispatchers.IO) {


            File("${uploadDir}/$fileName").writeBytes(fileBytes)

            true
        }

    }

    /**
     * Retrieves a file from our storage
     * @param fileName String Name of the file
     * @return Result<File, StorageError> File if Ok, StorageError if not
     */
    override suspend fun getFile(fileName: String): File? {
        logger.debug { "Get file: $fileName" }
        return withContext(Dispatchers.IO) {
            if (File("${uploadDir}/$fileName").exists()) {
                File("${uploadDir}/$fileName")
            } else {
                null
            }
        }
    }

    /**
     * Deletes a file from our storage
     * @param fileName String Name of the file
     * @return Result<String, StorageError> String if Ok, StorageError if not
     */
    override suspend fun deleteFile(fileName: String): Boolean {
        logger.debug { "Remove file: $fileName" }

        return withContext(Dispatchers.IO) {
            Files.deleteIfExists(Path.of("${uploadDir}/$fileName"))
            true
        }
    }

    fun PartData.FileItem.save(path: String): String {
        // read the file bytes
        val fileBytes = streamProvider().readBytes()
        // find the file extension eg: .jpg
        val fileExtension = originalFileName?.takeLastWhile { it != '.' }
        // generate a random name for the new file and append the file extension
        val fileName = UUID.randomUUID().toString() + "." + fileExtension
        // create our new file in the server
        val folder = File(path)
        // create parent directory if not exits
        if (!folder.parentFile.exists()) {
            folder.parentFile.mkdirs()
        }
        // continue with creating our new file
        folder.mkdir()
        // write bytes to our newly created file
        File("$path$fileName").writeBytes(fileBytes)
        return fileName
    }

}