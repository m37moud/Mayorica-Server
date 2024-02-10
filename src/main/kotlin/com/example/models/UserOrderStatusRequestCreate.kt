package com.example.models

data class UserOrderStatusRequestCreate(
    val approveState: Int,
    val totalAmount: Double,
    val takenAmount: Double,
    val note: String,
    val userAdminId: Int,

)
