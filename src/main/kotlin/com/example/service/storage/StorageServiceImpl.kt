package com.example.service.storage

import com.example.config.AppConfig
import com.example.utils.DeleteImageException
import com.example.utils.UploadImageException
import io.ktor.http.content.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.core.annotation.Singleton
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * Storage Service to manage our files
 * @property myConfig AppConfig Configuration of our service
 */
@Singleton
class StorageServiceImpl(
    private val myConfig: AppConfig
) : StorageService {
    private val uploadDir by lazy {
        myConfig.applicationConfiguration.propertyOrNull("storage.uploadDir")?.getString() ?: "uploads"
    }

    private val products by lazy { "$uploadDir/products" }
    private val news by lazy { "$uploadDir/news" }
    private val offers by lazy { "$uploadDir/offers" }
    private val categories by lazy { "$uploadDir/categories" }
    private val categoryIcons by lazy { "$uploadDir/categories/icons" }
    private val categoryImages by lazy { "$uploadDir/categories/images" }


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
        Files.createDirectories(Path.of(products))
        Files.createDirectories(Path.of(news))
        Files.createDirectories(Path.of(offers))
        Files.createDirectories(Path.of(categories))
        Files.createDirectories(Path.of(categoryIcons))
        Files.createDirectories(Path.of(categoryImages))
//        if (myConfig.propertyOrNull("ktor.environment")?.getString() == "dev") {
//            logger.debug { "Cleaning storage directory in $uploadDir" }
//            File(uploadDir).listFiles()?.forEach { it.delete() }
//            File("$uploadDir/products").listFiles()?.forEach { it.delete() }
//            File("$uploadDir/categories").listFiles()?.forEach { it.delete() }
//        }
    }

    fun checkOperation(path: String) = (File(path).exists())

    /**
     * Saves a file in our storage
     * @param fileName String Name of the file
     * @param fileUrl String URL of the file
     * @param fileBytes ByteArray Bytes of the file
     * @return Result<Map<String, String>, StorageError> Map if Ok, StorageError if not
     */
    override suspend fun saveProductImage(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteArray
    ): String? {
        logger.debug { "Saving file in: $fileName" }

        return withContext(Dispatchers.IO) {
            val path = "${products}/$fileName"
            File(path).writeBytes(fileBytes)
            if (checkOperation(path)) {
                fileUrl
            } else
                throw UploadImageException("Failed To Upload Image")

        }

    }

    override suspend fun saveCategoryIcons(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteArray
    ): String {
        logger.debug { "Saving file in: ${categoryIcons}/$fileName" }

        return withContext(Dispatchers.IO) {

            val path = "${categoryIcons}/$fileName"
            File(path).writeBytes(fileBytes)
            if (checkOperation(path)) {
                fileUrl
            } else
                throw UploadImageException("Failed To Upload Image")



        }

    }

    override suspend fun saveCategoryImages(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteArray
    ): String? {
        logger.debug { "Saving file in: $fileName" }

        return withContext(Dispatchers.IO) {
            val path = "${categoryImages}/$fileName"
            File(path).writeBytes(fileBytes)
            if (checkOperation(path)) {
                fileUrl
            } else
                throw UploadImageException("Failed To Upload Image")

        }

    }


    override suspend fun saveNewsImage(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteArray
    ): String? {
        logger.debug { "Saving file in: $fileName" }

        return withContext(Dispatchers.IO) {
            val path = "${news}/$fileName"
            File(path).writeBytes(fileBytes)
            if (checkOperation(path)) {
                fileUrl
            } else
                throw UploadImageException("Failed To Upload Image")

        }

    }

    override suspend fun saveOfferImage(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteArray
    ): String? {
        logger.debug { "Saving file in: $fileName" }

        return withContext(Dispatchers.IO) {
            val path = "${offers}/$fileName"
            File(path).writeBytes(fileBytes)
            if (checkOperation(path)) {
                fileUrl
            } else
                throw UploadImageException("Failed To Upload Image")

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
    override suspend fun deleteProductImage(fileName: String): Boolean {
        logger.debug { "deleteProductImage: $fileName" }

        return withContext(Dispatchers.IO) {
            val path = "${products}/$fileName"
            if (checkOperation(path)) {
                Files.deleteIfExists(Path.of(path))
            } else
                throw DeleteImageException("Failed To Delete Image")

        }
    }

    override suspend fun deleteCategoryIcons(fileName: String): Boolean {
        logger.debug { "deleteCategoryIcons Remove file:${categoryIcons}/$fileName" }

        return withContext(Dispatchers.IO) {
            val path = "${categoryIcons}/$fileName"
            if (checkOperation(path)) {
                Files.deleteIfExists(Path.of(path))
            } else
                throw DeleteImageException("Failed To Delete Image")


        }
    }

    override suspend fun deleteCategoryImages(fileName: String): Boolean {
        logger.debug { "deleteCategoryImages: $fileName" }

        return withContext(Dispatchers.IO) {
            val path = "${categoryImages}/$fileName"
            if (checkOperation(path)) {
                Files.deleteIfExists(Path.of(path))
            } else
                throw DeleteImageException("Failed To Delete Image")

        }
    }

    override suspend fun deleteNewsImages(fileName: String): Boolean {
        logger.debug { "deleteNewsImages: $fileName" }

        return withContext(Dispatchers.IO) {
            val path = "${news}/$fileName"
            if (checkOperation(path)) {
                Files.deleteIfExists(Path.of(path))
            } else
                throw DeleteImageException("Failed To Delete Image")
        }
    }

    override suspend fun deleteOfferImages(fileName: String): Boolean {
        logger.debug { "deleteOfferImages: $fileName" }

        return withContext(Dispatchers.IO) {
            val path = "${offers}/$fileName"
            if (checkOperation(path)) {
                Files.deleteIfExists(Path.of(path))
            } else
                throw DeleteImageException("Failed To Delete Image")
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