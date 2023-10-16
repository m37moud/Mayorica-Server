package com.example.database.table

import org.ktorm.schema.*

object UserOrderStatusEntity : Table<Nothing>("user_request_status") {
    val id = int("id").primaryKey()
    val requestUser_id = int("requestUser_id")
    val approve_state = int("approve_state")
    val approveDate = varchar("approveDate")
    val approveUpdateDate = varchar("approveUpdateDate")
    val approveByAdminId = int("approveByAdminId")
    val totalAmount = double("totalAmount")
    val takenAmount = double("takenAmount")
    val availableAmount = double("availableAmount")
    val note = varchar("note")

}