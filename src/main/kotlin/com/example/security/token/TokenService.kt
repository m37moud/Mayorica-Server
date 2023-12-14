package com.example.security.token

import com.auth0.jwt.JWTVerifier

interface TokenService {
    val audience :String
    val realm :String
     val issuer :String
     val expiresIn :Long
     val secret :String
    val appApiKey :String
    fun generateToken(
//        config: TokenConfig ,
        vararg claim: TokenClaim):String
    fun verifyJWT(): JWTVerifier?
}