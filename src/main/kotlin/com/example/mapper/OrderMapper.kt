package com.example.mapper

import com.example.models.CustomerOrderRevenue
import com.example.models.UserOrderStatus
import com.example.models.UserOrderStatusRequestCreate
import com.example.models.dto.CustomerOrderRevenueDto
import com.example.models.dto.UserOrderStatusRequestCreateDto
import com.example.models.request.order.UserOrderStatusRequest

fun UserOrderStatusRequestCreateDto.toModel(adminId: Int) =
    UserOrderStatusRequestCreate(
        approveState = approveState,
        totalAmount = totalAmount,
        takenAmount = takenAmount,
        note = note,
        userAdminId = adminId,
    )


fun CustomerOrderRevenue.toDto() = CustomerOrderRevenueDto(
    createdOrders, reviewingOrders, acceptedOrders, rejectedOrders
)
