package com.example.models.request

import com.example.models.AppsModel

data class AppsModelRequest(
    val packageName: String = "",
    val currentVersion: Double = 1.0,
    val forceUpdate: Boolean = false,
    val updateMessage: String = "",
    val enableApp: Boolean = true,
    val enableMessage: String = "",

    )


