package com.example.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class AboutUs(
    val id: Int = -1,
    val country: String,
    val governorate: String,
    val address: String,
    val telephone: String,
    val latitude: Double,
    val longitude: Double,
    val userAdminID: Int = -1,
    val createdAt: String = "",
    val updatedAt: String = "",
)
