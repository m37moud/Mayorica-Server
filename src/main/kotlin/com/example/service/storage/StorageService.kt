package com.example.service.storage

import java.io.File

interface StorageService {
    /**
     * save Ceramic Product Images
     * @param fileName random file name
     * @param fileUrl  file url
     * @param fileBytes file Bytes
     * @return String?
     */
    suspend fun saveProductImage(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteArray
    ): String?
    /**
     * save Type Category Images
     * @param fileName random file name
     * @param fileUrl  file url
     * @param fileBytes file Bytes
     * @return String?
     */
    suspend fun saveCategoryIcons(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteArray
    ): String

    /**
     * save Size Category Images
     * @param fileName random file name
     * @param fileUrl  file url
     * @param fileBytes file Bytes
     * @return String?
     */
    suspend fun saveCategoryImages(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteArray
    ): String?
    /**
     * save New Images
     * @param fileName random file name
     * @param fileUrl  file url
     * @param fileBytes file Bytes
     * @return String?
     */
    suspend fun saveNewsImage(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteArray
    ): String?
    /**
     * save Offer Images
     * @param fileName random file name
     * @param fileUrl  file url
     * @param fileBytes file Bytes
     * @return String?
     */
    suspend fun saveOfferImage(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteArray
    ): String?

    suspend fun getFile(fileName: String): File?
    /**
     * delete Ceramic Images
     * @param fileName random file name
     * @param fileUrl  file url
     * @param fileBytes file Bytes
     * @return String?
     */
    suspend fun deleteProductImage(fileName: String): Boolean
    /**
     * delete Type Category Images
     * @param fileName random file name
     * @param fileUrl  file url
     * @param fileBytes file Bytes
     * @return String?
     */
    suspend fun deleteCategoryIcons(fileName: String): Boolean
    /**
     * delete Size Category Images
     * @param fileName random file name
     * @param fileUrl  file url
     * @param fileBytes file Bytes
     * @return String?
     */
    suspend fun deleteCategoryImages(fileName: String): Boolean
    /**
     * delete News Images
     * @param fileName random file name
     * @param fileUrl  file url
     * @param fileBytes file Bytes
     * @return String?
     */
    suspend fun deleteNewsImages(fileName: String): Boolean
    /**
     * delete Offer Images
     * @param fileName random file name
     * @param fileUrl  file url
     * @param fileBytes file Bytes
     * @return String?
     */
    suspend fun deleteOfferImages(fileName: String): Boolean
}
