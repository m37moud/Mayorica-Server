package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class TypeCategoryMenu(
    val typeId: Int,
    val typeName: String
)
