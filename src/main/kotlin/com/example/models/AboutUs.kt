package com.example.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class AboutUs(
    val id: Int = -1,
    val title :String,
    val information :String,
    val userAdminID: Int = -1,
    val createdAt: String = "",
    val updatedAt: String = "",
)
