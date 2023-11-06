package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class HotReleaseProduct(
    val id: Int = -1,
    val productId: Int,
    val userAdminId: Int = -1,
    val createdAt: String = "",
    val updatedAt: String = ""
)
