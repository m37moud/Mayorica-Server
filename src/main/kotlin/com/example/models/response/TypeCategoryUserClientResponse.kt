package com.example.models.response

import kotlinx.serialization.Serializable

@Serializable
data class TypeCategoryUserClientResponse(
    val id: Int = -1,
    val typeName: String,
    val typeIcon: String,
)
