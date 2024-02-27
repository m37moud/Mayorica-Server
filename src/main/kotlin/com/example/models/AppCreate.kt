package com.example.models

data class AppCreate(
    val packageName: String,
    val currentVersion: Double,
    val forceUpdate: Boolean = false,
    val updateMessage: String = "",
    val enableApp: Boolean = true,
    val enableMessage: String = "",
    val userAdminId: Int,

)