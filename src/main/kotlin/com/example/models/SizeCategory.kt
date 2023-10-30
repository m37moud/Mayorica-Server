package com.example.models

import kotlinx.serialization.Serializable
@Serializable

data class SizeCategory(
    val id: Int = -1,
    val typeCategoryId: Int,
    val size: String,
    val sizeImage: String="",
    val userAdminID: Int,
    val createdAt: String="",
    val updatedAt: String=""
)

/**
 * TypeCategory DTO for response with pagination
 */
@Serializable
data class SizeCategoryPage(
    val page: Int,
    val perPage: Int,
    val data: List<SizeCategory>
)

