package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class UserOrderDto(
    val id: Int = -1,
    val requestUserId: Int = -1,
    val adminUserName: String,
    val fullName: String,
    val idNumber: String,
    val orderNumber: String,
    val department: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val governorate: String,
    val address: String,
    /**
     * approve state is state from 0 to 4
     * 0 -> order initialize
     * 1 -> order under reviewing
     * 2 -> order accepted
     * 3 -> order rejected
     */
    val approveState: Int = 0,
    val totalAmount: Double = 0.0,
    val takenAmount: Double = 0.0,
    val availableAmount: Double = 0.0,
    val note: String,
    val approveDate: String = "",
    val approveUpdateDate: String = "",
)