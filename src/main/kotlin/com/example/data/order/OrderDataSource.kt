package com.example.data.order

import com.example.models.UserOrder

interface OrderDataSource {

    suspend fun getAllOrder(): List<UserOrder>
    suspend fun getOrderById(id: Int): UserOrder?
    suspend fun getOrderByDate(createdDate: String): UserOrder?
    suspend fun getOrderByName(name: String): UserOrder?
    suspend fun getOrderByNameAndIdNumber(name: String , id_number:String): UserOrder?

    suspend fun createOrder(userOrder: UserOrder): Int

}