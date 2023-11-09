package com.example.service.storage

import java.io.File

interface StorageService {
    suspend fun saveProductImage(
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

    suspend fun saveNewsImage(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteArray
    ): String?
    suspend fun saveOfferImage(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteArray
    ): String?

    suspend fun getFile(fileName: String): File?
    suspend fun deleteProductImage(fileName: String): Boolean
    suspend fun deleteCategoryIcons(fileName: String): Boolean
    suspend fun deleteCategoryImages(fileName: String): Boolean
    suspend fun deleteNewsImages(fileName: String): Boolean
    suspend fun deleteOfferImages(fileName: String): Boolean
}
