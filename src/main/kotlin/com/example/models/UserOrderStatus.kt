package com.example.models

data class UserOrderStatus(
    val id: Int = -1,
    val requestUser_id: Int,
    val approve: Boolean = false,
    val approveDate: String = "",
    val approveUpdateDate: String = "",
    val approveByAdminId: Int = -1,
    val totalAmount: Double = 0.0,
    val takenAmount: Double = 0.0,
    val availableAmount: Double = 0.0,
)
