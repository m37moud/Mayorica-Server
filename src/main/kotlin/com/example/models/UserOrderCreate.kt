package com.example.models

data class UserOrderCreate(
    val fullName: String,
    val idNumber: String,
    val orderNumber: String,
    val department: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val city: String,
    val address: String,
    /**
     * approve state is state from (0 to 4)
     * 0 -> order initialize
     * 1 -> order under reviewing
     * 2 -> order accepted
     * 3 -> order rejected
     */
    val approveState: Int = 0,
    val sellerId: Int = 0,
)