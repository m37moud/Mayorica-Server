package com.example.models

import com.example.models.response.ProductResponse
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int = -1,
    val typeCategoryId: Int,
    val sizeCategoryId: Int,
    val colorCategoryId: Int,
    val userAdminID: Int,
    val productName: String,
    val image: String,
    val createdAt: String,
    val updatedAt: String,
    val deleted: Boolean = false

)

@Serializable
data class ProductPage(
    val page: Int,
    val perPage: Int,
    val data: List<Product>
)

@Serializable
data class ProductResponsePage(
    val page: Int,
    val perPage: Int,
    val data: List<ProductResponse>
)

