package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class UserOrderStatus(
    val id: Int = -1,
    val requestUser_id: Int,
    /**
     * approve state is state from 0 to 4
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
    val note: String = when (approveState) {
        0 -> {
            "your order is created successfully it take from one day to three days to approved"
        }
        1 -> {
            "your order is reviewing it take from one day to three days to approved"
        }
        2 -> {
            "your order is Accepted you Can Go To Nearest Ceramic Provider"
        }
        3 -> {
            "Apologize your order is Rejected you Can Contact With Sales on 01148588723 for more Information"
        }

        else -> {
            ""
        }
    }
)
