package com.example.models

data class ContractSection(
    val sectionName: String,
    val allowedAmount: Double,
    val allowedTotalRequest: Int,
    val userAdminId: Int,
)
