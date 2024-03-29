package com.example.models.options

import com.example.database.table.ProductEntity
import com.example.utils.MissingParameterException
import io.ktor.http.*
import org.ktorm.schema.Column

data class CeramicProductOptions(
    val page: Int?,
    val perPage: Int?,
    val query: String?,
    val byTypeCategoryId: Int?,
    val bySizeCategoryId: Int?,
    val byColorCategoryId: Int?,
    val isHot: Boolean?,
    val sortFiled: Column<*>?,
    val sortDirection: Int?,
)

fun getCeramicProductOptions(parameters: Parameters): CeramicProductOptions {
    val tempPage = parameters["page"]?.toIntOrNull() ?: 0
    val page = if (tempPage > 0) tempPage - 1 else 0
    val perPage = parameters["perPage"]?.toIntOrNull() ?: 10

    val query = parameters["query"]?.trim()
    val byTypeCategoryId = parameters["typeId"]?.toIntOrNull()
    val bySizeCategoryId = parameters["sizeId"]?.toIntOrNull()
    val byColorCategoryId = parameters["colorId"]?.toIntOrNull()
    val isHot = parameters["isHot"]?.toBoolean()

    val sortFiled = when (parameters["sort_by"] ?: "date") {
        "name" -> ProductEntity.productName
        "date" -> ProductEntity.createdAt
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
    return CeramicProductOptions(
        page,
        perPage,
        query,
        byTypeCategoryId,
        bySizeCategoryId,
        byColorCategoryId,
        isHot,
        sortFiled,
        sortDirection
    )
}
