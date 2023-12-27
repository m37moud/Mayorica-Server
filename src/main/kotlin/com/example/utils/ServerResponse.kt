package com.example.utils

import kotlinx.serialization.Serializable

@Serializable
data class ServerResponse<T>(
    val value: T?,
    val isSuccess: Boolean = true,
    val status: RespondStatus

) {
    companion object {
        fun error(errorMessage: Map<Int, String>?, code: Int) = ServerResponse(
            value = null,
            isSuccess = false,
            status = (RespondStatus(errorMessage = errorMessage, code = code))
        )

        inline fun <reified T> success(result: T?, successMessage: String?) = ServerResponse(
            value = result,
            isSuccess = true,
            status = (RespondStatus(successMessage = successMessage, code = 200))
        )
    }
    @Serializable
    data class RespondStatus(
        val errorMessage: Map<Int, String>? = null,
        val successMessage: String? = null,
        val code: Int?
    )
}



