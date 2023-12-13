package com.example.security.token

data class TokenConfig(
    val audience: String,
    val issuer: String,
    val expireIn: Long,
    val secret: String,
    val realm: String
)
