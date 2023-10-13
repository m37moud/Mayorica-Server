package com.example.data.order

import com.example.database.table.AdminUserEntity
import com.example.database.table.UserOrderEntity
import com.example.models.AdminUser
import com.example.models.UserOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.*

class MYSqlOrderDataSource(private val db: Database) : OrderDataSource {
    override suspend fun getAllOrder(): List<UserOrder> {
        return withContext(Dispatchers.IO) {
            val userOrders = db.from(UserOrderEntity)
                .select()
                .mapNotNull {
                    rowToUserOrder(it)
                }
            userOrders
        }
    }

    override suspend fun getOrderById(id: Int): UserOrder? {
        return withContext(Dispatchers.IO) {
            val userOrder = db.from(UserOrderEntity)
                .select()
                .where {
                    UserOrderEntity.id eq id
                }.map {
                    rowToUserOrder(it)
                }.firstOrNull()
            userOrder
        }
    }

    override suspend fun getOrderByDate(createdDate: String): UserOrder? {
        return withContext(Dispatchers.IO) {
            val userOrder = db.from(UserOrderEntity)
                .select()
                .where {
                    UserOrderEntity.created_at eq createdDate
                }.map {
                    rowToUserOrder(it)
                }.firstOrNull()
            userOrder
        }
    }

    override suspend fun getOrderByName(name: String): UserOrder? {
        return withContext(Dispatchers.IO) {
            val userOrder = db.from(UserOrderEntity)
                .select()
                .where {
                    UserOrderEntity.full_name eq name
                }.map {
                    rowToUserOrder(it)
                }.firstOrNull()
            userOrder
        }
    }


        override suspend fun createOrder(userOrder: UserOrder): Int {
           return withContext(Dispatchers.IO){
               val result = db.insert(UserOrderEntity){
                   set(it.full_name , userOrder.fullName)
                   set(it.id_number , userOrder.id_number)
                   set(it.department , userOrder.department)
                   set(it.country , userOrder.country)
                   set(it.governorate , userOrder.governorate)
                   set(it.created_at , userOrder.created_at)
                   set(it.update_at , userOrder.update_at)
               }
               result
           }
        }

    override suspend fun getOrderByNameAndIdNumber(name: String, id_number: String): UserOrder? {
        return withContext(Dispatchers.IO) {
            val userOrder = db.from(UserOrderEntity)
                .select()
                .where {
                    UserOrderEntity.full_name eq name
                    UserOrderEntity.id_number eq id_number

                }.map {
                    rowToUserOrder(it)
                }.firstOrNull()
            userOrder
        }
    }

    private fun rowToUserOrder(row: QueryRowSet?): UserOrder? {
            return if (row == null) {
                null
            } else {
                UserOrder(
                    row[UserOrderEntity.id] ?: -1,
                    row[UserOrderEntity.full_name] ?: "",
                    row[UserOrderEntity.id_number] ?: "",
                    row[UserOrderEntity.department] ?: "",
                    row[UserOrderEntity.country] ?: "",
                    row[UserOrderEntity.governorate] ?: "",
                    row[UserOrderEntity.created_at] ?: "",
                    row[UserOrderEntity.update_at] ?: "",
                )
            }
        }
    }