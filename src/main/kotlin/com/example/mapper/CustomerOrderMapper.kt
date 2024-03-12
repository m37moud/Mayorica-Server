package com.example.mapper

import com.example.models.UserOrderCreate
import com.example.models.request.order.UserOrderRequest


fun UserOrderRequest.toEntity(orderNumber: String) = UserOrderCreate(
    fullName = fullName,
    idNumber = idNumber,
    orderNumber = orderNumber,
    department = department,
    latitude = latitude,
    longitude = longitude,
    country = country,
    city = city,
    address = address,
    sellerId = sellerId
)