package com.example.data.contracting

import com.example.database.table.AdminUserEntity
import com.example.database.table.ColorCategoryEntity
import com.example.database.table.ContractSectionEntity
import com.example.database.table.UserOrderStatusEntity
import com.example.models.ContractSection
import com.example.models.dto.ContractSectionDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.core.annotation.Singleton
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.schema.Column

private val logger = KotlinLogging.logger { }

@Singleton
class MySqlContractingDataSource(private val db: Database) : ContractingDataSource {
    override suspend fun getAcceptedTotalCustomerOrder(): Int {
        logger.debug { "getAcceptedTotalCustomerOrder: called" }

        return withContext(Dispatchers.IO) {
            val result = db.from(UserOrderStatusEntity)
                .select()
                .where { UserOrderStatusEntity.approve_state eq 2 }// 2 = accepted state
                .mapNotNull { }
            result.size
        }
    }

    override suspend fun getContractSectionByIdDto(id: Int): ContractSectionDto? {
        logger.debug { "getAcceptedTotalCustomerOrder: called" }

        return withContext(Dispatchers.IO) {
            val result = db.from(ContractSectionEntity)
                .innerJoin(AdminUserEntity, on = ContractSectionEntity.userAdminId eq AdminUserEntity.id)

                .select(
                    ContractSectionEntity.id,
                    ContractSectionEntity.sectionName,
                    ContractSectionEntity.orderNumber,
                    ContractSectionEntity.allowedAmount,
//                    ContractSectionEntity.totalRequest,
                    ContractSectionEntity.allowedTotalRequest,
                    AdminUserEntity.username,
                    ContractSectionEntity.createdAt,
                    ColorCategoryEntity.updatedAt,
                )
                .where { ContractSectionEntity.id eq id }
                .map { rowToContractSectionDto(it) }
                .firstOrNull()
            result
        }
    }

    override suspend fun getContractSectionByName(name: String): ContractSection? {
        TODO("Not yet implemented")
    }

    override suspend fun getContractSectionPageable(
        query: String?,
        page: Int,
        perPage: Int,
        sortField: Column<*>,
        sortDirection: Int
    ): List<ContractSectionDto> {
        logger.debug { "getContractSectionPageable /page = $page perPage , perPage =$perPage" }
        val myLimit = if (perPage > 100) 100 else perPage
        val myOffset = (page * perPage)
        TODO("Not yet implemented")
    }

    override suspend fun addContractSection(contractSection: ContractSection): ContractSectionDto {
        TODO("Not yet implemented")
    }

    override suspend fun createContractSection(contractSection: ContractSection): Int {
        TODO("Not yet implemented")
    }

    override suspend fun updateContractSection(id: Int, contractSection: ContractSection): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteContractSection(Id: Int): Int {
        TODO("Not yet implemented")
    }

    private suspend fun rowToContractSectionDto(rowSet: QueryRowSet?): ContractSectionDto? {
        return if (rowSet == null) {
            null
        } else {
            ContractSectionDto(
                id = rowSet[ContractSectionEntity.id] ?: -1,
                sectionName = rowSet[ContractSectionEntity.sectionName] ?: "",
                orderNumber = rowSet[ContractSectionEntity.orderNumber] ?: "",
                allowedAmount = rowSet[ContractSectionEntity.allowedAmount] ?: 0.0,
                totalRequest = getAcceptedTotalCustomerOrder() ,
                allowedTotalRequest = rowSet[ContractSectionEntity.allowedTotalRequest] ?: -1,
                adminUserName = rowSet[AdminUserEntity.username] ?: "",
                createdAt = rowSet[ColorCategoryEntity.createdAt]?.toString() ?: "",
                updatedAt = rowSet[ColorCategoryEntity.createdAt]?.toString() ?: "",

                )

        }
    }
}