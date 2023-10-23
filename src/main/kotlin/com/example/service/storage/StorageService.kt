package com.example.service.storage

import java.io.File

interface StorageService {
    suspend fun saveFile(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteArray
    ): Boolean

    suspend fun getFile(fileName: String): File?
    suspend fun deleteFile(fileName: String): Boolean
}
