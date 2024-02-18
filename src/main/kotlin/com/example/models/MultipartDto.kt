package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class MultipartDto<T>(
    val data: T,
    val image: ByteArray? = null,
    val baseUrl: String,
    val fileName: String?,
)