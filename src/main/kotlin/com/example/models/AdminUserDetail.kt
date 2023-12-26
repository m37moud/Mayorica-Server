package com.example.models

data class AdminUserDetail(
    val id: Int = -1,
    val full_name: String,
    val username: String,
    val email: String,
    val phone: String,
    val role: String = Role.ADMIN.name,
    val created_at: String = "",
    val updated_at: String = ""
)
