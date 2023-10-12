package com.example.utils

import kotlinx.serialization.Serializable

@Serializable
data class MyResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)
