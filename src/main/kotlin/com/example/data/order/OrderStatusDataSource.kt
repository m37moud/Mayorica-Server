package com.example.data.order

import com.example.models.UserOrder
import com.example.models.UserOrderStatus

interface OrderStatusDataSource {
    suspend fun getOrderStatusByRequestUserId(requestUserId: Int): UserOrderStatus?
    suspend fun getAllOrderStatusByApprove(approveState: Int): List<UserOrderStatus>
    suspend fun updateOrderStatus(
        requestUserId: Int,
        userOrderStatus: UserOrderStatus
    ): Int

    suspend fun deleteOrderStatus(requestUserId: Int): Int

}