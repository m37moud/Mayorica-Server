package com.example.models.response

data class AcceptedOrderResponse(
    val fullName: String,
    val id_number: String,
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
    val created_at :String,
    val updated_at: String,
    val totalAmount: Double = 0.0,
    val takenAmount: Double = 0.0,
    val availableAmount: Double = 0.0,

    )
