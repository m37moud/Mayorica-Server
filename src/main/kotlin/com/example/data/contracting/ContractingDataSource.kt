package com.example.data.contracting

import com.example.models.ContractSection
import com.example.models.dto.ContractSectionDto
import org.ktorm.schema.Column

interface ContractingDataSource {
    suspend fun getAcceptedTotalCustomerOrder(): Int
    suspend fun getContractSectionByIdDto(id: Int): ContractSectionDto?

    suspend fun getContractSectionByNameDto(name: String): ContractSectionDto?

    suspend fun getContractSectionPageable(
        query: String?,
        page: Int,
        perPage: Int,
        sortField: Column<*>,
        sortDirection: Int
    ): List<ContractSectionDto>

    suspend fun addContractSection(contractSection: ContractSection): ContractSectionDto

    suspend fun createContractSection(contractSection: ContractSection): Int
    suspend fun updateContractSection(id: Int, contractSection: ContractSection): Int
    suspend fun deleteContractSection(id: Int): Int

}