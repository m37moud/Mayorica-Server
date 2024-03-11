package com.example.data.order

import com.example.database.table.*
import com.example.mapper.toUserResponse
import com.example.models.CeramicProvider
import com.example.models.UserOrder
import com.example.models.UserOrderCreate
import com.example.models.UserOrderStatus
import com.example.models.response.OrderResponse
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
                    UserOrderEntity.createdAt eq createdDate
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
                    UserOrderEntity.fullName eq name
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

    override suspend fun getOrderByOrderNumDto(orderNumber: String): OrderResponse? {
        return withContext(Dispatchers.IO) {
            val userOrderStatus = db.from(UserOrderStatusEntity)
//                .innerJoin(AdminUserEntity, on = UserOrderStatusEntity.approveByAdminId eq AdminUserEntity.id)
                .innerJoin(UserOrderEntity, on = UserOrderStatusEntity.requestUser_id eq UserOrderEntity.id)
                .innerJoin(CeramicProviderEntity, on = UserOrderEntity.sellerId eq CeramicProviderEntity.id)
                .select(
                    UserOrderEntity.fullName,
                    UserOrderEntity.idNumber,
                    UserOrderEntity.orderNumber,
                    UserOrderEntity.department,
                    UserOrderEntity.latitude,
                    UserOrderEntity.longitude,
                    UserOrderEntity.country,
                    UserOrderEntity.governorate,
                    UserOrderEntity.address,
                    UserOrderEntity.sellerId,
                    UserOrderStatusEntity.approve_state,

                    UserOrderStatusEntity.totalAmount,
                    UserOrderStatusEntity.takenAmount,
                    UserOrderStatusEntity.availableAmount,
                    UserOrderStatusEntity.note,
                    UserOrderStatusEntity.approveDate,
                    UserOrderStatusEntity.approveUpdateDate,


                    )
                .where {
                    UserOrderEntity.orderNumber eq orderNumber
                }.map {
                    rowToUserOrderDto(it)
                }.firstOrNull()
            userOrderStatus
        }
    }

    override suspend fun getOrderByNameAndIdNumber(name: String, idNumber: String): UserOrder? {
        return withContext(Dispatchers.IO) {
            val userOrder = db.from(UserOrderEntity)
                .select()
                .where {
                    (UserOrderEntity.fullName eq name) and
                            (UserOrderEntity.idNumber eq idNumber)

                }.map {
                    rowToUserOrder(it)
                }.firstOrNull()
            userOrder
        }
    }

    override suspend fun updateOrder(userOrder: UserOrder): Int {
        return withContext(Dispatchers.IO) {
            val result = db.update(UserOrderEntity) {
                set(it.fullName, userOrder.fullName)
                set(it.idNumber, userOrder.idNumber)
                set(it.department, userOrder.department)
                set(it.country, userOrder.country)
                set(it.governorate, userOrder.city)
                set(it.address, userOrder.address)
                set(it.approveState, userOrder.approveState)
                set(it.updatedAt, LocalDateTime.now())
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
    override suspend fun createOrderWithOrderStatus(userOrder: UserOrderCreate): Int {
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

    suspend fun createUserOrder(userOrder: UserOrderCreate): Int {
        return withContext(Dispatchers.IO) {
            val insertResult = db.insert(UserOrderEntity) {
                set(it.fullName, userOrder.fullName)
                set(it.idNumber, userOrder.idNumber)
                set(it.orderNumber, userOrder.orderNumber)
                set(it.department, userOrder.department)
                set(it.latitude, userOrder.latitude)
                set(it.longitude, userOrder.longitude)
                set(it.country, userOrder.country)
                set(it.governorate, userOrder.city)
                set(it.address, userOrder.address)
                set(it.approveState, userOrder.approveState)
                set(it.createdAt, LocalDateTime.now())
                set(it.updatedAt, LocalDateTime.now())
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
                    UserOrderEntity.idNumber eq idNumber
                }.map {
                    rowToUserOrder(it)
                }.firstOrNull()
            order
        }

    }

    suspend fun getCeramicProviderByID(id: Int): CeramicProvider? {
        return withContext(Dispatchers.IO) {
            val provider = db.from(CeramicProviderEntity)
                .select()
                .where {
                    CeramicProviderEntity.id eq id
                }.map {
                    rowToCeramicProvider(it)
                }.firstOrNull()
            provider
        }
    }
    private fun rowToUserOrder(row: QueryRowSet?): UserOrder? {
        return if (row == null) {
            null
        } else {
            UserOrder(
                id = row[UserOrderEntity.id] ?: -1,
                fullName = row[UserOrderEntity.fullName] ?: "",
                idNumber = row[UserOrderEntity.idNumber] ?: "",
                orderNumber = row[UserOrderEntity.orderNumber] ?: "",
                department = row[UserOrderEntity.department] ?: "",
                latitude = row[UserOrderEntity.latitude] ?: 0.0,
                longitude = row[UserOrderEntity.longitude] ?: 0.0,
                country = row[UserOrderEntity.country] ?: "",
                city = row[UserOrderEntity.governorate] ?: "",
                address = row[UserOrderEntity.address] ?: "",
                approveState = row[UserOrderEntity.approveState] ?: 0,
                sellerId = row[UserOrderEntity.sellerId] ?: -1,
                createdAt = row[UserOrderEntity.createdAt]?.toString() ?: "",
                updatedAt = row[UserOrderEntity.updatedAt]?.toString() ?: "",
            )
        }
    }

    private suspend fun rowToUserOrderDto(row: QueryRowSet?): OrderResponse? {
        return if (row == null) {
            null
        } else {
            val customerName = row[UserOrderEntity.fullName] ?: ""
            val customerIdNumber = row[UserOrderEntity.idNumber] ?: ""
            val department = row[UserOrderEntity.department] ?: ""
            val latitude = row[UserOrderEntity.latitude] ?: 0.0
            val longitude = row[UserOrderEntity.longitude] ?: 0.0
            val country = row[UserOrderEntity.country] ?: ""
            val city = row[UserOrderEntity.governorate] ?: ""
            val address = row[UserOrderEntity.address] ?: ""
            val sellerId = row[UserOrderEntity.sellerId] ?: -1
            val approveState = row[UserOrderStatusEntity.approve_state] ?: -1
            val totalAmount = row[UserOrderStatusEntity.totalAmount] ?: 0.0
            val takenAmount = row[UserOrderStatusEntity.takenAmount] ?: 0.0
            val availableAmount = row[UserOrderStatusEntity.availableAmount] ?: 0.0
            val note = row[UserOrderStatusEntity.note] ?: ""
            val approveDate = row[UserOrderStatusEntity.approveDate]?.toString() ?: ""
            val approveUpdateDate = row[UserOrderStatusEntity.approveUpdateDate]?.toString() ?: ""
            val seller = getCeramicProviderByID(sellerId)?.toUserResponse()

            OrderResponse(
                fullName = customerName,
                idNumber = customerIdNumber,
                department = department,
                latitude = latitude,
                longitude = longitude,
                country = country,
                city = city,
                address = address,
                seller = seller,
                approveState = approveState,
                totalAmount = totalAmount,
                takenAmount = takenAmount,
                availableAmount = availableAmount,
                note = note,
                createdAt = approveDate.toString(),
                updatedAt = approveUpdateDate.toString(),

                )
        }
    }
    private fun rowToCeramicProvider(row: QueryRowSet?): CeramicProvider? {
        return if (row == null)
            null
        else {
            val id = row[CeramicProviderEntity.id] ?: -1
            val adminUserId = row[CeramicProviderEntity.userAdminID] ?: -1
            val name = row[CeramicProviderEntity.name] ?: ""
            val latitude = row[CeramicProviderEntity.latitude] ?: 0.0
            val longitude = row[CeramicProviderEntity.longitude] ?: 0.0
            val country = row[CeramicProviderEntity.country] ?: ""
            val governorate = row[CeramicProviderEntity.governorate] ?: ""
            val address = row[CeramicProviderEntity.address] ?: ""
            val createdAt = row[CeramicProviderEntity.createdAt] ?: ""
            val updatedAt = row[CeramicProviderEntity.updatedAt] ?: ""

            CeramicProvider(
                id = id,
                userAdminID = adminUserId,
                name = name,
                latitude = latitude,
                longitude = longitude,
                country = country,
                city = governorate,
                address = address,
                createdAt = createdAt.toString(),
                updatedAt = updatedAt.toString()

            )
        }
    }


}