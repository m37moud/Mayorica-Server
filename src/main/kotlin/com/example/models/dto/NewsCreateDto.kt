package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class NewsCreateDto(
    val newsTitle: String,
    val newsDescription: String,
    val newsImageUrl: String?,
)
