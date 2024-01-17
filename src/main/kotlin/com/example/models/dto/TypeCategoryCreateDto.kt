package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class TypeCategoryCreateDto(
    val typeName: String,
    val iconUrl: String,
)
