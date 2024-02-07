package com.example.database.table

import org.ktorm.schema.*

object ContractSectionEntity : Table<Nothing>("t_contract_section") {
    val id =int("id").primaryKey()
    val sectionName = varchar("sectionName")
    val orderNumber = varchar("orderNumber")
    val allowedAmount = double("allowedAmount")
//    val totalRequest = int("totalRequest")
    val allowedTotalRequest = int("allowedTotalRequest")
    val userAdminId = int("userAdminId")
    val createdAt = datetime("createdAt")
    val updatedAt = datetime("updatedAt")
}