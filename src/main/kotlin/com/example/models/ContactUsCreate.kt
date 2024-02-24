package com.example.models

data class ContactUsCreate(
    val country: String,
    val governorate: String,
    val address: String,
    val telephone: String,
    val email: String,
    val mapLabel: String,
    val latitude: Double,
    val longitude: Double,
    val webLink: String,
    val fbLink: String,
    val youtubeLink: String,
    val instagramLink: String,
    val linkedInLink: String,
    val userAdminId: Int,

    )