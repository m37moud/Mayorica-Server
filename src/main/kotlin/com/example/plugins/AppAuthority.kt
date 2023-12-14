package com.example.plugins

import com.example.security.token.TokenService
import com.example.utils.AuthenticationException
import io.ktor.server.auth.*
import io.ktor.server.config.*
import dev.forst.ktor.apikey.apiKey

/**
 * I created a User and [UserWithApp] specifically for my Android app.
 * When I call this from my Android app, I use the API key that
 * I got for my [UserWithApp]. This API key is also saved in the Ktor
 * app config file.
 */
fun AuthenticationConfig.configureAppAuthority(
        jwtService: TokenService
) {


//    val appApiKey = config.propertyOrNull("ktor.appAuth.apiKey")?.getString() ?: ""

    apiKey("app") {
//        challenge {
//
//            throw AuthenticationException()
//        }

        validate { keyFromHeader ->
            if (keyFromHeader == jwtService.appApiKey)
                AuthPrincipal(true)
            else
                null
        }
    }
}