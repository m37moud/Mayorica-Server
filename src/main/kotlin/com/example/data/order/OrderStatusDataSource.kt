package com.example.data.order

import com.example.models.UserOrderDto
import com.example.models.UserOrderStatus
import org.ktorm.schema.Column

interface OrderStatusDataSource {
    suspend fun getOrderStatusByRequestUserId(requestUserId: Int): UserOrderStatus?
    suspend fun getOrderStatusByRequestUserIdDto(requestUserId: Int): UserOrderDto?
    suspend fun getNumberOfOrders(): Int

    suspend fun getAllCustomerOrderPageable(
        query: String?,
        page: Int,
        perPage: Int,
        byApproveStatue: Int?,
        sortField: Column<*>,
        sortDirection: Int
    ): List<UserOrderDto>
    suspend fun getAllOrderStatusByApprove(approveState: Int): List<UserOrderStatus>
    suspend fun updateOrderStatus(
        requestUserId: Int,
        userOrderStatus: UserOrderStatus
    ): Int

    suspend fun deleteOrderStatus(requestUserId: Int): Int

}