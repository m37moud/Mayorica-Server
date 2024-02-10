package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserOrderStatusRequestCreateDto(
    val approveState: Int,
    val totalAmount: Double,
    val takenAmount: Double,
    val note: String
)
