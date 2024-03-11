package com.example.mapper

import com.example.models.UserOrderCreate
import com.example.models.request.order.UserOrderRequest


fun UserOrderRequest.toEntity(orderNumber: String) = UserOrderCreate(
    fullName, idNumber, orderNumber, department, latitude, longitude, country, city, address
)