package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class CustomerOrderRevenueDto(
    val createdOrders: Double,
    val reviewingOrders: Double,
    val acceptedOrders: Double,
    val rejectedOrders: Double,
)
