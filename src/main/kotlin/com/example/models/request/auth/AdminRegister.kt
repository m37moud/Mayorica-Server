package com.example.models.request.auth

import com.example.models.Role
import kotlinx.serialization.Serializable

@Serializable
data class AdminRegister(
    val full_name: String,
    val username: String,
    val password: String,
    val role: String ,
)
