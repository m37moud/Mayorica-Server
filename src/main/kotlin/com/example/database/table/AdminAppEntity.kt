package com.example.database.table

import org.ktorm.schema.*

object AdminAppEntity : Table<Nothing>("t_mobile_app") {
    val id = int("id").primaryKey()
    val packageName = varchar("packageName")
    val apiKey = varchar("apiKey")
    val currentVersion = double("current_version")
    val forceUpdate = boolean("force_update")
    val updateMessage= varchar("update_message")
    val enableApp = boolean("enable_app")
    val enableMessage= varchar("enable_message")
    val userAdminId = int("admin_id")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}