package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.ext.inject

fun AuthenticationConfig.adminClientAuth(
    jwtService: TokenService
) {

    jwt {
        jwtService.verifyJWT()
        realm = jwtService.realm
//        verifier(
//            JWT
//                .require(Algorithm.HMAC256(config.secret))
//                .withAudience(config.audience)
//                .withIssuer(config.issuer)
//                .build()
//        )
        validate { credential ->
            if (credential.payload.audience.contains(jwtService.audience))
                JWTPrincipal(credential.payload)
            else
                null
        }
    }


}