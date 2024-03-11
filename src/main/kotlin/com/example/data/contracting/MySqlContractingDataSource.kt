package com.example.data.contracting

import com.example.database.table.*
import com.example.models.ContractSection
import com.example.models.dto.ContractSectionDto
import com.example.utils.AlreadyExistsException
import com.example.utils.ErrorException
import com.example.utils.NotFoundException
import com.example.utils.generateContractNumber
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
                    ContractSectionEntity.updatedAt,
                )
                .where { ContractSectionEntity.id eq id }
                .map { rowToContractSectionDto(it) }
                .firstOrNull()
            result
        }
    }

    override suspend fun getContractSectionByNameDto(name: String): ContractSectionDto? {
        logger.debug { "getContractSectionByNameDto : called" }
        return withContext(Dispatchers.IO) {
            val result = db.from(ContractSectionEntity)
                .innerJoin(AdminUserEntity, on = ContractSectionEntity.userAdminId eq AdminUserEntity.id)
                .select(
                    ContractSectionEntity.id,
                    ContractSectionEntity.sectionName,
                    ContractSectionEntity.orderNumber,
                    ContractSectionEntity.allowedAmount,
                    ContractSectionEntity.allowedTotalRequest,
                    AdminUserEntity.username,
                    ContractSectionEntity.createdAt,
                    ContractSectionEntity.updatedAt,
                )
                .where { ContractSectionEntity.sectionName eq name }
                .map { rowToContractSectionDto(it) }
                .firstOrNull()
            result

        }
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
        return withContext(Dispatchers.IO) {
            val result = db.from(ContractSectionEntity)
                .innerJoin(AdminUserEntity, on = ContractSectionEntity.userAdminId eq AdminUserEntity.id)
                .select(
                    ContractSectionEntity.id,
                    ContractSectionEntity.sectionName,
                    ContractSectionEntity.orderNumber,
                    ContractSectionEntity.allowedAmount,
                    ContractSectionEntity.allowedTotalRequest,
                    AdminUserEntity.username,
                    ContractSectionEntity.createdAt,
                    ContractSectionEntity.updatedAt,
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
                        it += (UserOrderEntity.fullName like "%%${query}")
                    }
                }
                .mapNotNull { rowToContractSectionDto(it) }
            result
        }
    }

    override suspend fun addContractSection(contractSection: ContractSection): ContractSectionDto {
        logger.debug { "addContractSection : called" }
        if (getContractSectionByNameDto(contractSection.sectionName) != null)
            throw AlreadyExistsException("this Section inserted before .")
        if (createContractSection(contractSection) < 0) throw ErrorException("Failed to create New Size Category .")
        return getContractSectionByNameDto(contractSection.sectionName)
            ?: throw NotFoundException("failed to get Section after created.")

    }

    override suspend fun createContractSection(contractSection: ContractSection): Int {
        logger.debug { "createContractSection : called" }
        return withContext(Dispatchers.IO) {
            val result = db.insert(ContractSectionEntity) {
                set(it.sectionName, contractSection.sectionName)
                set(it.orderNumber, generateContractNumber())
                set(it.allowedAmount, contractSection.allowedAmount)
                set(it.allowedTotalRequest, contractSection.allowedTotalRequest)
                set(it.userAdminId, contractSection.userAdminId)
                set(it.createdAt, LocalDateTime.now())
                set(it.updatedAt, LocalDateTime.now())
            }
            result
        }
    }

    override suspend fun updateContractSection(id: Int, contractSection: ContractSection): Int {
        logger.debug { "updateContractSection : called" }
        return withContext(Dispatchers.IO) {
            val result = db.update(ContractSectionEntity) {
                set(it.sectionName, contractSection.sectionName)
                set(it.allowedAmount, contractSection.allowedAmount)
                set(it.allowedTotalRequest, contractSection.allowedTotalRequest)
                set(it.userAdminId, contractSection.userAdminId)
                set(it.updatedAt, LocalDateTime.now())

                where {
                    it.id eq id
                }
            }
            result
        }

    }

    override suspend fun deleteContractSection(id: Int): Int {
        logger.debug { "deleteContractSection : called" }
        return withContext(Dispatchers.IO) {
            val result = db.delete(ContractSectionEntity) {
                it.id eq id
            }
            result
        }
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
                totalRequest = getAcceptedTotalCustomerOrder(),
                allowedTotalRequest = rowSet[ContractSectionEntity.allowedTotalRequest] ?: -1,
                adminUserName = rowSet[AdminUserEntity.username] ?: "",
                createdAt = rowSet[ColorCategoryEntity.createdAt]?.toString() ?: "",
                updatedAt = rowSet[ColorCategoryEntity.createdAt]?.toString() ?: "",

                )

        }
    }
}