package com.example.models

import com.example.utils.toListOfIntOrNull
import com.example.utils.toListOfStringOrNull
import io.ktor.http.*
import kotlinx.serialization.Serializable


@Serializable
data class UserOptions(
    val page: Int?,
    val limit: Int?,
    val query: String?,
    val permissions: List<Int>?,
    val country: List<String>?
)


fun getUserOptions(parameters: Parameters): UserOptions {
    val page = parameters["page"]?.toIntOrNull()
    val limit = parameters["limit"]?.toIntOrNull()

    val query = parameters["query"]?.trim()
    val permissions = parameters["permissions"].toListOfIntOrNull()
    val countries = parameters["countries"].toListOfStringOrNull()
    return UserOptions(page, limit, query, permissions, countries)
}
