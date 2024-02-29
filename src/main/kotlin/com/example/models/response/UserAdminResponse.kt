package com.example.models.response

import kotlinx.serialization.Serializable

@Serializable
data class UserAdminResponse(
    val id: Int,
    val username: String,
    val fullName: String,
    val role: String,
    val createdAt: String,
)
