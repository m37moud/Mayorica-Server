package com.example.models

data class AppsModel(
    val id : Int = -1,
    val packageName :String,
    val apiKey :String,
    val currentVersion :Double,
    val forceUpdate :Boolean,
    val updateMessage :String,
    val enableApp :Boolean,
    val enableMessage :String,
    val userAdminId: Int = -1,
    val createdAt: String = "",
    val updatedAt: String = "",
)
