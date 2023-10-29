package com.example.service.storage

import java.io.File

interface StorageService {
    suspend fun saveProductFile(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteArray
    ): String?
    suspend fun saveCategoryIcons(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteArray
    ): String?

    suspend fun saveCategoryImages(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteArray
    ): String?

    suspend fun getFile(fileName: String): File?
    suspend fun deleteFile(fileName: String): Boolean
}
