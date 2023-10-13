package com.example.models.request.order

data class UserOrderRequest(
    val full_name: String,
    val id_number: String,
    val department: String,
    val country: String,
    val governorate: String
)
