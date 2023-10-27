package com.example.data.order

import com.example.models.UserOrder
import com.example.models.UserOrderStatus

interface OrderDataSource {
    /**
     * authenticate is required
     */
    suspend fun getAllOrder(): List<UserOrder>
    suspend fun getOrderById(id: Int): UserOrder?
    suspend fun getOrderByDate(createdDate: String): UserOrder?
    suspend fun getOrderByName(name: String): UserOrder?
    suspend fun getOrderByOrderNum(orderNumber: String): UserOrder?
    suspend fun getOrderByNameAndIdNumber(name: String, id_number: String): UserOrder?

    suspend fun updateOrder(userOrder: UserOrder): Int
    suspend fun deleteOrder(orderId: Int): Int

    /**
     *  no authenticate is required
     */
    suspend fun createOrderWithOrderStatus(userOrder: UserOrder): Int

    //    suspend fun createOrderStatus(userOrderStatus: UserOrderStatus): Int
    suspend fun getOrderByIdNumber(idNumber: String): UserOrder?


}