package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class UserOrderStatus(
    val id: Int = -1,
    val requestUser_id: Int,
    /**
     * approve state is state from 0 to 3
     * 0 -> order initialize
     * 1 -> order under reviewing
     * 2 -> order accepted
     * 3 -> order rejected
     */
    val approveState: Int = 0,
    val approveDate: String = "",
    val approveUpdateDate: String = "",
    val approveByAdminId: Int = -1,
    val totalAmount: Double = 0.0,
    val takenAmount: Double = 0.0,
    val availableAmount: Double = 0.0,
    val note: String = if (approveState == 0) "your order is created successfully it take from one day to three days to approved" else ""
)
