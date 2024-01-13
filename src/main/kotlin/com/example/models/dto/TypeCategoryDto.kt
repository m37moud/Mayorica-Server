package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class TypeCategoryDto(
    val id: Int = -1,
    val adminUserName: String = "",
    val typeName: String,
    val typeIcon: String,
    val createdAt: String,
    val updatedAt: String
)
