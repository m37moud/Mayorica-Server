package com.example.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserTokensResponse(
    @SerialName("accessTokenExpirationDate") val accessTokenExpirationDate: Long,
    @SerialName("refreshTokenExpirationDate") val refreshTokenExpirationDate: Long,
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String
)