package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class AppsModelDto(
    val id: Int,
    val adminUserName: String,
    val packageName: String,
    val apiKey: String,
    val currentVersion: Double,
    val forceUpdate: Boolean,
    val updateMessage: String,
    val enableApp: Boolean,
    val enableMessage: String,
    val createdAt: String,
    val updatedAt: String,

    )