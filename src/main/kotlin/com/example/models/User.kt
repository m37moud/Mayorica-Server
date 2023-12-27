package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int = -1,
    val full_name: String,
    val username: String,
    val email: String,
    val phone: String,
    val password: String,
    val salt: String,
    val role: String = Role.ADMIN.name,
    val created_at: String = "",
    val updated_at: String = ""

)