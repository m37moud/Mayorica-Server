package com.example.data.order

import com.example.database.table.UserOrderEntity
import com.example.database.table.UserOrderStatusEntity
import com.example.models.UserOrder
import com.example.models.UserOrderStatus
import com.example.utils.toDatabaseString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.time.LocalDateTime

class MYSqlOrderStatusDataSource(private val db: Database) : OrderStatusDataSource {


    override suspend fun getOrderStatusByRequestUserId(requestUserId: Int): UserOrderStatus? {
        return withContext(Dispatchers.IO) {
            val userOrderStatus = db.from(UserOrderStatusEntity)

                .select()
                .where {
                    UserOrderStatusEntity.requestUser_id eq requestUserId
                }.map {
                    rowToUserOrderStatus(it)
                }.firstOrNull()
            userOrderStatus
        }
    }

    override suspend fun getAllOrderStatusByApprove(approveState: Int): List<UserOrderStatus> {
        return withContext(Dispatchers.IO) {
            val userOrderStatusList = db.from(UserOrderStatusEntity)
                .select()
                .where {
                    UserOrderStatusEntity.approve_state eq approveState
                }
                .mapNotNull {
                    rowToUserOrderStatus(it)
                }
            userOrderStatusList
        }
    }

    override suspend fun updateOrderStatus(
        requestUserId: Int,
        userOrderStatus: UserOrderStatus
    ): Int {
        return withContext(Dispatchers.IO) {
            val result = db.update(UserOrderStatusEntity) {
                set(it.approve_state, userOrderStatus.approveState)
//                set(it.approveDate, userOrderStatus.approveDate)
                set(it.approveUpdateDate, LocalDateTime.now())
                set(it.approveByAdminId, userOrderStatus.approveByAdminId)
                set(it.totalAmount, userOrderStatus.totalAmount)
                set(it.takenAmount, userOrderStatus.takenAmount)
                set(it.availableAmount, userOrderStatus.availableAmount)
                set(it.note, userOrderStatus.note)
                where {
                    it.requestUser_id eq requestUserId
                }
            }


            result
        }
    }

    override suspend fun deleteOrderStatus(requestUserId: Int): Int {
        return withContext(Dispatchers.IO) {
            val result = db.delete(UserOrderStatusEntity) {
                it.id eq requestUserId
            }
            result
        }
    }

    private fun rowToUserOrderStatus(row: QueryRowSet?): UserOrderStatus? {
        return if (row == null) {
            null
        } else {
            UserOrderStatus(
                row[UserOrderStatusEntity.id] ?: -1,
                row[UserOrderStatusEntity.requestUser_id] ?: -1,
                row[UserOrderStatusEntity.approve_state] ?: 0,
                row[UserOrderStatusEntity.approveDate]?.toDatabaseString() ?: "",
                row[UserOrderStatusEntity.approveUpdateDate]?.toDatabaseString() ?: "",
                row[UserOrderStatusEntity.approveByAdminId] ?: -1,
                row[UserOrderStatusEntity.totalAmount] ?: 0.0,
                row[UserOrderStatusEntity.takenAmount] ?: 0.0,
                row[UserOrderStatusEntity.availableAmount] ?: 0.0,
                row[UserOrderStatusEntity.note] ?: "",
            )
        }
    }
}