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
    val city: String,
    val address: String,
//    val approveState: Int = 0,
    val sellerId: Int = -1,

    )
