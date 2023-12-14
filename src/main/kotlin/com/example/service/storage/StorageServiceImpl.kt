package com.example.service.storage

import io.ktor.http.content.*
import io.ktor.server.config.*
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
    private val myConfig: HoconApplicationConfig
) : StorageService {
    private val uploadDir by lazy {
        myConfig.propertyOrNull("storage.uploadDir")?.getString() ?: "uploads"
    }

    private val products by lazy { "$uploadDir/products" }
    private val news by lazy { "$uploadDir/news" }
    private val offers by lazy { "$uploadDir/offers" }
    private val categories by lazy { "$uploadDir/categories" }
    private val categoryIcons by lazy { "$uploadDir/categories/icons" }
    private val categoryImages by lazy { "$uploadDir/categories/images" }

    /**
     * delete variable
     */
    private val deleteProducts by lazy { "image/products" }
    private val deleteNews by lazy { "image/news" }
    private val deleteOffers by lazy { "image/offers" }
    private val deleteCategoryIcons by lazy { "image/categories/icons" }
    private val deleteCategoryImages by lazy { "image/categories/images" }

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


            File("${products}/$fileName").writeBytes(fileBytes)

            fileUrl
        }

    }
    override suspend fun saveCategoryIcons(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteArray
    ): String? {
        logger.debug { "Saving file in: $fileName" }

        return withContext(Dispatchers.IO) {


            File("${categoryIcons}/$fileName").writeBytes(fileBytes)

            fileUrl
        }

    }
    override suspend fun saveCategoryImages(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteArray
    ): String? {
        logger.debug { "Saving file in: $fileName" }

        return withContext(Dispatchers.IO) {


            File("${categoryImages}/$fileName").writeBytes(fileBytes)

            fileUrl
        }

    }


    override suspend fun saveNewsImage(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteArray
    ): String? {
        logger.debug { "Saving file in: $fileName" }

        return withContext(Dispatchers.IO) {


            File("${news}/$fileName").writeBytes(fileBytes)

            fileUrl
        }

    }
    override suspend fun saveOfferImage(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteArray
    ): String? {
        logger.debug { "Saving file in: $fileName" }

        return withContext(Dispatchers.IO) {


            File("${offers}/$fileName").writeBytes(fileBytes)

            fileUrl
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
            Files.deleteIfExists(Path.of("${deleteProducts}/$fileName"))
            true
        }
    }

    override suspend fun deleteCategoryIcons(fileName: String): Boolean {
        logger.debug { "Remove file: $fileName" }

        return withContext(Dispatchers.IO) {
            Files.deleteIfExists(Path.of("${deleteCategoryIcons}/$fileName"))
            true
        }
    }

    override suspend fun deleteCategoryImages(fileName: String): Boolean {
        logger.debug { "deleteCategoryImages: $fileName" }

        return withContext(Dispatchers.IO) {
            Files.deleteIfExists(Path.of("${deleteCategoryImages}/$fileName"))
            true
        }
    }
    override suspend fun deleteNewsImages(fileName: String): Boolean {
        logger.debug { "deleteProductImage: $fileName" }

        return withContext(Dispatchers.IO) {
            Files.deleteIfExists(Path.of("${deleteNews}/$fileName"))
            true
        }
    }
    override suspend fun deleteOfferImages(fileName: String): Boolean {
        logger.debug { "deleteProductImage: $fileName" }

        return withContext(Dispatchers.IO) {
            Files.deleteIfExists(Path.of("${deleteOffers}/$fileName"))
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