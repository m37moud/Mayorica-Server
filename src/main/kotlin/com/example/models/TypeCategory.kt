package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class TypeCategory(
    val id: Int = -1,
    val typeName: String,
    val typeIcon: String,
    val userAdminID: Int,
    val createdAt: String,
    val updatedAt: String
)

/**
 * TypeCategoryPage DTO for response with pagination
 */
@Serializable
data class TypeCategoryPage(
    val page: Int,
    val perPage: Int,
    val data: List<TypeCategory>
)



