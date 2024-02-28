package com.example.models

data class CustomerOrderRevenue(
    val createdOrders: Double = 0.0,
    val reviewingOrders: Double = 0.0,
    val acceptedOrders: Double = 0.0,
    val rejectedOrders: Double = 0.0,
)