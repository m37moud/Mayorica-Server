package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class NewsDto(
    val id: Int = -1,
    val title: String,
    val image: String? = null,
    val newsDescription: String,
    val adminUserName: String,
    val createdAt: String,
    val updatedAt: String,
)
