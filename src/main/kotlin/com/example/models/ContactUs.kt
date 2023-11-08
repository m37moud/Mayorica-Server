package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class ContactUs(
    val id: Int = -1,
    val country: String,
    val governorate: String,
    val address: String,
    val telephone: String,
    val email: String,
    val latitude: Double,
    val longitude: Double,
    val fbLink: String,
    val youtubeLink: String,
    val instagramLink: String,
    val linkedInLink: String,
    val userAdminID: Int = -1,
    val createdAt: String = "",
    val updatedAt: String = "",

)
