package com.example.models.response

data class RejectedOrderResponse(
    val fullName: String,
    val idNumber: String,
    val department: String,
    val country: String,
    val governorate: String,
    /**
     * approve state is state from (0 to 4)
     * 0 -> order initialize
     * 1 -> order under reviewing
     * 2 -> order accepted
     * 3 -> order rejected
     */
    val approveState: Int = 0,
    val createdAt: String,
    val updatedAt: String,
    val reason: String
)
