package com.example.utils

import java.security.MessageDigest
import java.util.*

fun isImageContentType(contentType: String): Boolean {
    // Implement content type validation, e.g., using Apache Tika or a MIME type library
    // Return true for valid image types, false for others
    // Example: Check if the content type starts with "image/"
    return contentType.startsWith("image/")
}

fun generateSafeFileName(originalFileName: String): String {
    // Sanitize the file name to prevent directory traversal attacks
    val uuid = UUID.randomUUID().toString()
    val fileExtension = originalFileName.substringAfterLast(".", "")
//    val hash = MessageDigest.getInstance("SHA-256").digest(uuid.toString().toByteArray()).joinToString("") { "%02x".format(it) }
    return "$uuid.$fileExtension"
}