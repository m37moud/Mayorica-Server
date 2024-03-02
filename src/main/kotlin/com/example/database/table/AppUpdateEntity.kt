package com.example.database.table

import com.example.database.table.MobileAppEntity.primaryKey
import org.ktorm.schema.*

object AppUpdateEntity  : Table<Nothing>("t_app_update"){
    val id = int("id").primaryKey()
    val updateVersion = double("updateVersion")
    val forceUpdate = boolean("forceUpdate")
    val updateMessage= varchar("updateMessage")

}