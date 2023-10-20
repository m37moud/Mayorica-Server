package com.example.models

data class CeramicProvider(
    val id: Int = -1,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val governorate: String,
    val address: String
)
