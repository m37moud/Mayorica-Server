package com.example.models

data class NewsCreate(
    val newsTitle: String,
    val newsDescription: String,
    val newsImageUrl: String?,
    val userAdminId: Int,
)
