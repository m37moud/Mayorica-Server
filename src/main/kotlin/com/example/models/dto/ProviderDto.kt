package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProviderDto(
    val id: Int = -1,
    val adminUserName: String = "",
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val country: String = "",
    val governorate: String = "",
    val address: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""

)
