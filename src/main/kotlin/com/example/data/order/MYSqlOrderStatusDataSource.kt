package com.example.data.order

import com.example.database.table.*
import com.example.models.CustomerOrderRevenue
import com.example.models.UserOrderDto
import com.example.models.UserOrderStatus
import com.example.models.UserOrderStatusRequestCreate
import com.example.utils.toDatabaseString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.core.annotation.Singleton
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.schema.Column
import java.time.LocalDateTime

private val logger = KotlinLogging.logger { }

@Singleton
class MYSqlOrderStatusDataSource(private val db: Database) : OrderStatusDataSource {

    private suspend fun getAdminUserName(id: Int): String? {
        logger.debug { "getAdminUserName : called" }
        return withContext(Dispatchers.IO) {
            val result = db.from(AdminUserEntity)
                .select(
                    AdminUserEntity.username
                )
                .where {
                    AdminUserEntity.id eq id
                }
                .map {
                    it[AdminUserEntity.username]
                }
                .firstOrNull()
            result

        }

    }

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

    override suspend fun getOrderStatusByRequestUserIdDto(requestUserId: Int): UserOrderDto? {
        return withContext(Dispatchers.IO) {
            val userOrderStatus = db.from(UserOrderStatusEntity)
//                .innerJoin(AdminUserEntity, on = UserOrderStatusEntity.approveByAdminId eq AdminUserEntity.id)
                .innerJoin(UserOrderEntity, on = UserOrderStatusEntity.requestUser_id eq UserOrderEntity.id)
                .select(
                    UserOrderStatusEntity.id,
                    UserOrderEntity.id,
                    UserOrderStatusEntity.approveByAdminId,
                    UserOrderEntity.fullName,
                    UserOrderEntity.idNumber,
                    UserOrderEntity.orderNumber,
                    UserOrderEntity.department,
                    UserOrderEntity.latitude,
                    UserOrderEntity.longitude,
                    UserOrderEntity.country,
                    UserOrderEntity.governorate,
                    UserOrderEntity.address,
                    UserOrderStatusEntity.approve_state,
                    UserOrderStatusEntity.totalAmount,
                    UserOrderStatusEntity.takenAmount,
                    UserOrderStatusEntity.availableAmount,
                    UserOrderStatusEntity.note,
                    UserOrderStatusEntity.approveDate,
                    UserOrderStatusEntity.approveUpdateDate,


                    )
                .where {
                    UserOrderStatusEntity.requestUser_id eq requestUserId
                }.map {
                    rowToUserOrderStatueDto(it)
                }.firstOrNull()
            userOrderStatus
        }
    }

    override suspend fun getNumberOfOrders(): Int {
        logger.debug { "getNumberOfProduct" }
        return withContext(Dispatchers.IO) {
            val result = db.from(UserOrderStatusEntity)
                .select()
                .mapNotNull { rowToUserOrderStatus(it) }
            result.size
        }
    }

    override suspend fun getAllCustomerOrderPageable(
        query: String?,
        page: Int,
        perPage: Int,
        byApproveStatue: Int?,
        sortField: Column<*>,
        sortDirection: Int
    ): List<UserOrderDto> {
        logger.debug { "getAllCustomerOrderPageable /page = $page perPage , perPage =$perPage" }
        val myLimit = if (perPage > 100) 100 else perPage
        val myOffset = (page * perPage)
        return withContext(Dispatchers.IO) {
            val result = db.from(UserOrderStatusEntity)
//                .innerJoin(AdminUserEntity, on = UserOrderStatusEntity.approveByAdminId eq AdminUserEntity.id)
                .innerJoin(UserOrderEntity, on = UserOrderStatusEntity.requestUser_id eq UserOrderEntity.id)
                .select(
                    UserOrderStatusEntity.id,
                    UserOrderEntity.id,
                    UserOrderStatusEntity.approveByAdminId,
                    UserOrderEntity.fullName,
                    UserOrderEntity.idNumber,
                    UserOrderEntity.orderNumber,
                    UserOrderEntity.department,
                    UserOrderEntity.latitude,
                    UserOrderEntity.longitude,
                    UserOrderEntity.country,
                    UserOrderEntity.governorate,
                    UserOrderEntity.address,
                    UserOrderStatusEntity.approve_state,
                    UserOrderStatusEntity.totalAmount,
                    UserOrderStatusEntity.takenAmount,
                    UserOrderStatusEntity.availableAmount,
                    UserOrderStatusEntity.note,
                    UserOrderStatusEntity.approveDate,
                    UserOrderStatusEntity.approveUpdateDate,
                )
                .limit(myLimit)
                .offset(myOffset)
                .orderBy(
                    if (sortDirection > 0)
                        sortField.asc()
                    else
                        sortField.desc()
                )
                .whereWithConditions {
                    if (!query.isNullOrEmpty()) {
                        it += (UserOrderEntity.fullName like "%%${query}") or
                                (UserOrderEntity.orderNumber like "%${query}%")

                    }
                    if (byApproveStatue != null) {
                        it += (UserOrderStatusEntity.approve_state eq byApproveStatue)
                    }
                }
                .mapNotNull { rowToUserOrderStatueDto(it) }
            result

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
        userOrderStatus: UserOrderStatusRequestCreate
    ): Int {
        return withContext(Dispatchers.IO) {
            val result = db.useTransaction {
                updateCustomerOrderStatus(requestUserId, userOrderStatus)
                updateCustomerOrder(requestUserId, userOrderStatus.approveState)
            }
            result
        }
    }

    private suspend fun updateCustomerOrderStatus(
        requestUserId: Int,
        userOrderStatus: UserOrderStatusRequestCreate
    ): Int {
        return withContext(Dispatchers.IO) {
            val result = db.update(UserOrderStatusEntity) {
                set(it.approve_state, userOrderStatus.approveState)
//                set(it.approveDate, userOrderStatus.approveDate)
                set(it.totalAmount, userOrderStatus.totalAmount)
                set(it.takenAmount, userOrderStatus.takenAmount)
                set(
                    it.availableAmount,
                    getAvailableAmount(
                        total = userOrderStatus.totalAmount,
                        taken = userOrderStatus.takenAmount
                    )
                )
                set(it.note, userOrderStatus.note)
                set(it.approveByAdminId, userOrderStatus.userAdminId)
                set(it.approveUpdateDate, LocalDateTime.now())

                where {
                    it.requestUser_id eq requestUserId
                }
            }


            result
        }
    }

    private suspend fun updateCustomerOrder(id: Int, approveState: Int): Int {
        return withContext(Dispatchers.IO) {
            val result = db.update(UserOrderEntity) {
                set(it.approveState, approveState)
                where {
                    it.id eq id
                }
            }
            result
        }

    }

    private fun getAvailableAmount(total: Double, taken: Double): Double {
        return total.minus(taken)

    }

    override suspend fun deleteOrderStatus(requestUserId: Int): Int {
        return withContext(Dispatchers.IO) {
            val result = db.delete(UserOrderStatusEntity) {
                it.id eq requestUserId
            }
            result
        }
    }

    override suspend fun getCustomerOrderDetails(): CustomerOrderRevenue {
        return withContext(Dispatchers.IO) {
            val customerOrderRevenue = db.useTransaction {
                val create = getAllCreatedOrders()
                val review = getAllReviewingOrders()
                val accept = getAllAcceptedOrders()
                val reject = getAllRejectedOrders()
                CustomerOrderRevenue(
                    createdOrders = create,
                    reviewingOrders = review,
                    acceptedOrders = accept,
                    rejectedOrders = reject,
                )
            }
            customerOrderRevenue
        }

    }

    private suspend fun getAllCreatedOrders(): Double {
        return withContext(Dispatchers.IO) {
            val result = db.from(UserOrderStatusEntity)
                .select()
                .where {
                    UserOrderStatusEntity.approve_state eq 0
                }
                .map { rowToUserOrderStatus(it) }
            result?.size?.toDouble() ?: 0.0
        }

    }

    private suspend fun getAllReviewingOrders(): Double {
        return withContext(Dispatchers.IO) {
            val result = db.from(UserOrderStatusEntity)
                .select()
                .where {
                    UserOrderStatusEntity.approve_state eq 1
                }
                .mapNotNull { rowToUserOrderStatus(it) }
            result?.size?.toDouble() ?: 0.0

        }

    }

    private suspend fun getAllAcceptedOrders(): Double {
        return withContext(Dispatchers.IO) {
            val result = db.from(UserOrderStatusEntity)
                .select()
                .where {
                    UserOrderStatusEntity.approve_state eq 2
                }
                .mapNotNull { rowToUserOrderStatus(it) }
            result?.size?.toDouble() ?: 0.0

        }

    }

    private suspend fun getAllRejectedOrders(): Double {
        return withContext(Dispatchers.IO) {
            val result = db.from(UserOrderStatusEntity)
                .select()
                .where {
                    UserOrderStatusEntity.approve_state eq 3
                }
                .mapNotNull { rowToUserOrderStatus(it) }
            result?.size?.toDouble() ?: 0.0

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

    private suspend fun rowToUserOrderStatueDto(row: QueryRowSet?): UserOrderDto? {
        return if (row == null) {
            null
        } else {
            val id = row[UserOrderStatusEntity.id] ?: -1
            val requestUserId = row[UserOrderEntity.id] ?: -1
            val adminUserName = getAdminUserName(id = row[UserOrderStatusEntity.approveByAdminId] ?: -1) ?: ""
            val customerName = row[UserOrderEntity.fullName] ?: ""
            val customerIdNumber = row[UserOrderEntity.idNumber] ?: ""
            val orderNumber = row[UserOrderEntity.orderNumber] ?: ""
            val department = row[UserOrderEntity.department] ?: ""
            val latitude = row[UserOrderEntity.latitude] ?: 0.0
            val longitude = row[UserOrderEntity.longitude] ?: 0.0
            val country = row[UserOrderEntity.country] ?: ""
            val governorate = row[UserOrderEntity.governorate] ?: ""
            val address = row[UserOrderEntity.address] ?: ""
            val approveState = row[UserOrderStatusEntity.approve_state] ?: -1
            val totalAmount = row[UserOrderStatusEntity.totalAmount] ?: 0.0
            val takenAmount = row[UserOrderStatusEntity.takenAmount] ?: 0.0
            val availableAmount = row[UserOrderStatusEntity.availableAmount] ?: 0.0
            val note = row[UserOrderStatusEntity.note] ?: ""
            val approveDate = row[UserOrderStatusEntity.approveDate]?.toString() ?: ""
            val approveUpdateDate = row[UserOrderStatusEntity.approveUpdateDate]?.toString() ?: ""


            UserOrderDto(
                id = id,
                requestUserId = requestUserId,
                adminUserName = adminUserName,
                fullName = customerName,
                idNumber = customerIdNumber,
                orderNumber = orderNumber,
                department = department,
                latitude = latitude,
                longitude = longitude,
                country = country,
                governorate = governorate,
                address = address,
                approveState = approveState,
                totalAmount = totalAmount,
                takenAmount = takenAmount,
                availableAmount = availableAmount,
                note = note,
                approveDate = approveDate.toString(),
                approveUpdateDate = approveUpdateDate.toString(),

                )
        }
    }
}