package com.example.models.options

import com.example.database.table.AdminUserEntity
import com.example.utils.MissingParameterException
import io.ktor.http.*
import org.ktorm.schema.Column

data class AdminUsersOptions(
    val query: String?,
    val permission: String?,
    val page: Int?,
    val perPage: Int?,
    val sortFiled: Column<*>?,
    val sortDirection: Int?,
)
fun getAdminUserOptions(parameters: Parameters): AdminUsersOptions {
    val tempPage = parameters["page"]?.toIntOrNull() ?: 0
    val page = if (tempPage > 0) tempPage - 1 else 0
    val perPage = parameters["perPage"]?.toIntOrNull() ?: 10

    val query = parameters["query"]?.trim()
    val permission: String? = parameters["permission"]?.trim()


    val sortFiled = when (parameters["sort_by"] ?: "date") {
        "name" -> AdminUserEntity.full_name
        "username" -> AdminUserEntity.username
        "date" -> AdminUserEntity.created_at
        else -> {
            throw MissingParameterException("invalid parameter for sort_by chose between (name & date)")
        }
    }
    val sortDirection = when (parameters["sort_direction"] ?: "dec") {
        "dec" -> -1
        "asc" -> 1
        else -> {
            throw MissingParameterException("invalid parameter for sort_direction chose between (dec & asc)")

        }
    }
    return AdminUsersOptions(
        query,
        permission,
        page,
        perPage,
        sortFiled,
        sortDirection
    )
}
