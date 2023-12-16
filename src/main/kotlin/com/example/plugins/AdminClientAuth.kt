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
        // Load the token verification config
        jwtService.verifyJWT()?.let { verifier(it) }
        // With realm we can get the token from the request
        realm = jwtService.realm

        // If the token is valid, it also has the indicated audience,
        // and has the user's field to compare it with the one we want
        // return the JWTPrincipal, otherwise return null
        validate { credential ->
            if (credential.payload.audience.contains(jwtService.audience))
                JWTPrincipal(credential.payload)
            else
                null
        }
    }


}