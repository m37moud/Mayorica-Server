package com.example.utils

import org.koin.core.annotation.Single
import org.koin.core.annotation.Singleton

@Singleton
class ImageValidator {
    fun isValid(name: String?) : Boolean {
        val extension = name?.substringAfterLast(".", "")
        val isImage = extension?.let { it in listOf("jpg", "jpeg", "png","webp") } ?: false
        return if (isImage) true else throw Exception("Invalid File")
    }
}