package com.example.models.request.order

import kotlinx.serialization.Serializable

@Serializable
data class UserOrderRequest(
    val fullName: String,
    val idNumber: String,
    val department: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val governorate: String,
    val address: String,
    val approve_state: Int = 0,

    )
