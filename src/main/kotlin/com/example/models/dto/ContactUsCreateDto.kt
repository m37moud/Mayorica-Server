package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class ContactUsCreateDto(
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
)