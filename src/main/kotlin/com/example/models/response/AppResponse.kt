package com.example.models.response

import com.example.models.AppsModel
import kotlinx.serialization.Serializable

@Serializable
data class AppResponse(
    val id: Int = -1,
    val packageName: String = "",
    val currentVersion: Double = 0.0,
    val forceUpdate: Boolean = false,
    val updateMessage: String = "",
    val enableApp: Boolean = false,
    val enableMessage: String = "",
)

fun AppsModel.toResponse() = AppResponse(
    id = this.id,
    packageName = this.packageName,
    currentVersion = this.currentVersion,
    forceUpdate = this.forceUpdate,
    updateMessage = this.updateMessage,
    enableApp = this.enableApp,
    enableMessage = this.enableMessage,
    )
