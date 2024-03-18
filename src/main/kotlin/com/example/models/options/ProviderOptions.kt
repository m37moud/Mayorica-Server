package com.example.models.options

import com.example.database.table.CeramicProviderEntity
import com.example.database.table.ProductEntity
import com.example.utils.MissingParameterException
import io.ktor.http.*
import org.ktorm.schema.Column

data class ProviderOptions(
    val page: Int?,
    val perPage: Int?,
    val query: String?,
    val latitude: Double?,
    val longitude: Double?,
    val sortFiled: Column<*>?,
    val sortDirection: Int?,
)
fun getProviderOptions(parameters: Parameters): ProviderOptions {
    val tempPage = parameters["page"]?.toIntOrNull() ?: 0
    val page = if (tempPage > 0) tempPage - 1 else 0
    val perPage = parameters["perPage"]?.toIntOrNull() ?: 10

    val query = parameters["query"]?.trim()
    val latitude = parameters["latitude"]?.toDoubleOrNull()
    val longitude = parameters["longitude"]?.toDoubleOrNull()

    val sortFiled = when (parameters["sort_by"] ?: "date") {
        "name" -> CeramicProviderEntity.name
        "date" -> CeramicProviderEntity.createdAt
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
    return ProviderOptions(
        page = page,
        perPage = perPage,
        query = query,
        latitude = latitude,
        longitude = longitude,
        sortFiled = sortFiled,
        sortDirection = sortDirection
    )
}
