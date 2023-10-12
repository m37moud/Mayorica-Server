package com.example.utils

data class MyResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)
