package com.example.security.token

import com.auth0.jwt.JWTVerifier
import com.example.models.response.UserTokensResponse
import com.example.utils.TokenType

interface TokenService {
    val audience: String
    val realm: String
    val issuer: String
    val expiresIn: Long
    val refreshIn: Long
    val secret: String
    val appApiKey: String
    fun generateToken(
//        config: TokenConfig ,
        tokenType: TokenType,
        vararg claim: TokenClaim
    ): String

    fun verifyJWT(): JWTVerifier?
    fun generateUserTokens(
        vararg claim: TokenClaim
    ): UserTokensResponse
}