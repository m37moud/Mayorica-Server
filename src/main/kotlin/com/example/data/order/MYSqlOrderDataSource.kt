package com.example.data.order

import com.example.database.table.AdminUserEntity
import com.example.database.table.UserOrderEntity
import com.example.database.table.UserOrderStatusEntity
import com.example.models.AdminUser
import com.example.models.UserOrder
import com.example.models.UserOrderStatus
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


    override suspend fun getOrderByNameAndIdNumber(name: String, id_number: String): UserOrder? {
        return withContext(Dispatchers.IO) {
            val userOrder = db.from(UserOrderEntity)
                .select()
                .where {
                    (UserOrderEntity.full_name eq name) and
                            (UserOrderEntity.id_number eq id_number)

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
                set(it.id_number, userOrder.id_number)
                set(it.department, userOrder.department)
                set(it.country, userOrder.country)
                set(it.governorate, userOrder.governorate)
                set(it.approve_state, userOrder.approveState)
                set(it.created_at, userOrder.created_at)
                set(it.updated_at, userOrder.updated_at)
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
                    id_number = userOrder.id_number
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
                set(it.id_number, userOrder.id_number)
                set(it.department, userOrder.department)
                set(it.country, userOrder.country)
                set(it.governorate, userOrder.governorate)
                set(it.approve_state, userOrder.approveState)
                set(it.created_at, userOrder.created_at)
                set(it.updated_at, userOrder.updated_at)
            }
            insertResult
        }

    }

     suspend fun createOrderStatus(userOrderStatus: UserOrderStatus): Int {
        return withContext(Dispatchers.IO) {
            val result = db.insert(UserOrderStatusEntity) {
                set(it.requestUser_id, userOrderStatus.requestUser_id)
                set(it.approve_state, userOrderStatus.approveState)
                set(it.approveDate, userOrderStatus.approveDate)
                set(it.approveUpdateDate, userOrderStatus.approveUpdateDate)
                set(it.approveByAdminId, userOrderStatus.approveByAdminId)
                set(it.totalAmount, userOrderStatus.totalAmount)
                set(it.takenAmount, userOrderStatus.takenAmount)
                set(it.availableAmount, userOrderStatus.availableAmount)
                set(it.note, userOrderStatus.note)
            }
            result
        }
    }

    override suspend fun getOrderByIdNumber(idNumber: String): UserOrder?{
        return withContext(Dispatchers.IO){
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
                row[UserOrderEntity.department] ?: "",
                row[UserOrderEntity.country] ?: "",
                row[UserOrderEntity.governorate] ?: "",
                row[UserOrderEntity.approve_state] ?: 0,
                row[UserOrderEntity.created_at] ?: "",
                row[UserOrderEntity.updated_at] ?: "",
            )
        }
    }


}