package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class AppsModel(
    val id : Int = -1,
    val packageName: String = "",
    val apiKey: String = "",
    val currentVersion: Double = 1.0,
    val forceUpdate: Boolean = false,
    val updateMessage: String = "",
    val enableApp: Boolean = true,
    val enableMessage: String = "",
    val userAdminId: Int = -1,
    val createdAt: String = "",
    val updatedAt: String = "",
)
