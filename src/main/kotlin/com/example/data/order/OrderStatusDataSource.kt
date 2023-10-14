package com.example.data.order

import com.example.models.UserOrderStatus

interface OrderStatusDataSource {
    suspend fun createOrderStatus(userOrderStatus: UserOrderStatus):Int
    suspend fun getAllOrderStatusByRequestUserId(requestUserId:Int):UserOrderStatus?
    suspend fun getAllOrderStatusByApprove(approve:Boolean):List<UserOrderStatus>

}