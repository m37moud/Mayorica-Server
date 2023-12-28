package com.example.models

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable

@Serializable
data class AdminUser(
    val id: Int = -1,
    val full_name: String,
    val username: String,
    val password: String,
    val salt: String,
    val role: String = Role.ADMIN.name,
    val created_at: String = "",
    val updated_at: String = ""

) : Principal

enum class Role {
    SUPERADMIN, ADMIN, USER, UNKNOWN
}