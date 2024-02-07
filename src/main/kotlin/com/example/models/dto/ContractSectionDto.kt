package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class ContractSectionDto(
    val id: Int,
    val sectionName: String,
    val orderNumber: String,
    val allowedAmount: Double,
    val totalRequest: Int,
    val allowedTotalRequest: Int,
    val adminUserName: String,
    val createdAt: String,
    val updatedAt: String
)
