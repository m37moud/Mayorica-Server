package com.example.models.options

import com.example.database.table.AdminAppEntity
import com.example.database.table.MobileAppEntity
import com.example.database.table.NewsEntity
import com.example.utils.MissingParameterException
import io.ktor.http.*
import org.ktorm.schema.Column

data class AppsControlOption(
    val page: Int?,
    val perPage: Int?,
    val query: String?,
    val sortFiled: Column<*>?,
    val sortDirection: Int?,
)
fun getAppsOptions(parameters: Parameters): AppsControlOption {
    val tempPage = parameters["page"]?.toIntOrNull() ?: 0
    val page = if (tempPage > 0) tempPage - 1 else 0
    val perPage = parameters["perPage"]?.toIntOrNull() ?: 10

    val query = parameters["query"]?.trim()

    val sortFiled = when (parameters["sort_by"] ?: "date") {
        "name" -> MobileAppEntity.packageName
        "date" -> MobileAppEntity.createdAt
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
    return AppsControlOption(
        page,
        perPage,
        query,
        sortFiled,
        sortDirection
    )
}
