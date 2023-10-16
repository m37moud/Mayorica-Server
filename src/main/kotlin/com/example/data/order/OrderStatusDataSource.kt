package com.example.data.order

import com.example.models.UserOrderStatus

interface OrderStatusDataSource {
    suspend fun getAllOrderStatusByRequestUserId(requestUserId:Int):UserOrderStatus?
    suspend fun getAllOrderStatusByApprove(approveState: Int):List<UserOrderStatus>

}