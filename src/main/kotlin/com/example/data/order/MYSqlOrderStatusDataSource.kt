package com.example.data.order

import com.example.database.table.UserOrderEntity
import com.example.database.table.UserOrderStatusEntity
import com.example.models.UserOrder
import com.example.models.UserOrderStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.*

class MYSqlOrderStatusDataSource(private val db: Database) : OrderStatusDataSource {


    override suspend fun getAllOrderStatusByRequestUserId(requestUserId: Int): UserOrderStatus? {
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

    private fun rowToUserOrderStatus(row: QueryRowSet?): UserOrderStatus? {
        return if (row == null) {
            null
        } else {
            UserOrderStatus(
                row[UserOrderStatusEntity.id] ?: -1,
                row[UserOrderStatusEntity.requestUser_id] ?: -1,
                row[UserOrderStatusEntity.approve_state] ?: 0,
                row[UserOrderStatusEntity.approveDate] ?: "",
                row[UserOrderStatusEntity.approveUpdateDate] ?: "",
                row[UserOrderStatusEntity.approveByAdminId] ?: -1,
                row[UserOrderStatusEntity.totalAmount] ?: 0.0,
                row[UserOrderStatusEntity.takenAmount] ?: 0.0,
                row[UserOrderStatusEntity.availableAmount] ?: 0.0,
                row[UserOrderStatusEntity.note] ?: "",
            )
        }
    }
}