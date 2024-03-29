package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class ContactUs(
    val id: Int = -1,
    val country: String = "",
    val governorate: String = "",
    val address: String = "",
    val telephone: String = "",
    val email: String = "",
    val mapLabel: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val webLink: String = "",
    val fbLink: String = "",
    val youtubeLink: String = "",
    val instagramLink: String = "",
    val linkedInLink: String = "",
    val userAdminID: Int = -1,
    val createdAt: String = "",
    val updatedAt: String = "",

    )
