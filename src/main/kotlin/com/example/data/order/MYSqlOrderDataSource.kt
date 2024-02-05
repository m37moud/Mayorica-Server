package com.example.data.order

import com.example.database.table.*
import com.example.models.UserOrder
import com.example.models.UserOrderStatus
import com.example.utils.toDatabaseString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Singleton
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.time.LocalDateTime

@Singleton
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

    override suspend fun getOrderByDate(createdDate: LocalDateTime): UserOrder? {
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

    override suspend fun getOrderByOrderNum(orderNumber: String): UserOrder? {
        return withContext(Dispatchers.IO) {
            val userOrder = db.from(UserOrderEntity)
                .select()
                .where {
                    UserOrderEntity.orderNumber eq orderNumber
                }.map {
                    rowToUserOrder(it)
                }.firstOrNull()
            userOrder
        }
    }

    override suspend fun getOrderByNameAndIdNumber(name: String, idNumber: String): UserOrder? {
        return withContext(Dispatchers.IO) {
            val userOrder = db.from(UserOrderEntity)
                .select()
                .where {
                    (UserOrderEntity.full_name eq name) and
                            (UserOrderEntity.id_number eq idNumber)

                }.map {
                    rowToUserOrder(it)
                }.firstOrNull()
            userOrder
        }
    }

    override suspend fun updateOrder(userOrder: UserOrder): Int {
        return withContext(Dispatchers.IO) {
            val result = db.update(UserOrderEntity) {
                set(it.full_name, userOrder.fullName)
                set(it.id_number, userOrder.idNumber)
                set(it.department, userOrder.department)
                set(it.country, userOrder.country)
                set(it.governorate, userOrder.governorate)
                set(it.address, userOrder.address)
                set(it.approve_state, userOrder.approveState)
                set(it.updated_at, LocalDateTime.now())
                where {
                    it.id eq userOrder.id
                }
            }


            result
        }
    }

    override suspend fun deleteOrder(orderId: Int): Int {
        return withContext(Dispatchers.IO) {

            val result = db.delete(UserOrderEntity) {
                it.id eq orderId

            }

            result

        }
    }

    /**
     *  no authenticate is required
     */
    override suspend fun createOrderWithOrderStatus(userOrder: UserOrder): Int {
        return withContext(Dispatchers.IO) {
            val result = db.useTransaction {
                val insertResult = createUserOrder(userOrder)
                val tempUserOrder = getOrderByNameAndIdNumber(
                    name = userOrder.fullName,
                    idNumber = userOrder.idNumber
                )
                val tempOrderStatus = UserOrderStatus(
                    requestUser_id = tempUserOrder!!.id,
                )
                createOrderStatus(userOrderStatus = tempOrderStatus)

            }

            result
        }
    }

    suspend fun createUserOrder(userOrder: UserOrder): Int {
        return withContext(Dispatchers.IO) {
            val insertResult = db.insert(UserOrderEntity) {
                set(it.full_name, userOrder.fullName)
                set(it.id_number, userOrder.idNumber)
                set(it.orderNumber, userOrder.orderNumber)
                set(it.department, userOrder.department)
                set(it.latitude, userOrder.latitude)
                set(it.longitude, userOrder.longitude)
                set(it.country, userOrder.country)
                set(it.governorate, userOrder.governorate)
                set(it.address, userOrder.address)
                set(it.approve_state, userOrder.approveState)
                set(it.created_at, LocalDateTime.now())
                set(it.updated_at, LocalDateTime.now())
            }
            insertResult
        }

    }


    suspend fun createOrderStatus(userOrderStatus: UserOrderStatus): Int {
        return withContext(Dispatchers.IO) {
            val result = db.insert(UserOrderStatusEntity) {
                set(it.requestUser_id, userOrderStatus.requestUser_id)
                set(it.approve_state, userOrderStatus.approveState)
                set(it.approveDate, LocalDateTime.now())
                set(it.approveUpdateDate, LocalDateTime.now())
                set(it.approveByAdminId, userOrderStatus.approveByAdminId)
                set(it.totalAmount, userOrderStatus.totalAmount)
                set(it.takenAmount, userOrderStatus.takenAmount)
                set(it.availableAmount, userOrderStatus.availableAmount)
                set(it.note, userOrderStatus.note)
            }
            result
        }
    }

    override suspend fun getOrderByIdNumber(idNumber: String): UserOrder? {
        return withContext(Dispatchers.IO) {
            val order = db.from(UserOrderEntity)
                .select()
                .where {
                    UserOrderEntity.id_number eq idNumber
                }.map {
                    rowToUserOrder(it)
                }.firstOrNull()
            order
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
                row[UserOrderEntity.orderNumber] ?: "",
                row[UserOrderEntity.department] ?: "",
                row[UserOrderEntity.latitude] ?: 0.0,
                row[UserOrderEntity.longitude] ?: 0.0,
                row[UserOrderEntity.country] ?: "",
                row[UserOrderEntity.governorate] ?: "",
                row[UserOrderEntity.address] ?: "",
                row[UserOrderEntity.approve_state] ?: 0,
                row[UserOrderEntity.created_at]?.toDatabaseString() ?: "",
                row[UserOrderEntity.updated_at]?.toDatabaseString() ?: "",
            )
        }
    }


}