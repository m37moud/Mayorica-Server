package com.example.models

data class UserOrder(
    val id: Int = -1,
    val fullName: String,
    val id_number: String,
    val department: String,
    val country: String,
    val governorate: String,
    val created_at :String,
    val updated_at: String
)
