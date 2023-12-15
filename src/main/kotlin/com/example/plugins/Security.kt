package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.administrations.apps.AppsDataSource
import com.example.models.response.AppResponse
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import org.koin.ktor.ext.inject


// principal for the app
data class AppPrincipal(val mobileApp: AppResponse) : Principal
data class AuthPrincipal(val isValid: Boolean) : Principal

fun Application.configureSecurity(
//    config: TokenConfig,
//    appConfig: HoconApplicationConfig,
//    app: AppsDataSource
) {
    // Inject the token service
    val jwtService: TokenService by inject()
    val app: AppsDataSource by inject()

    // Please read the jwt property from the config file if you are using EngineMain
    install(Authentication) {
        configureAppAuthority(jwtService)
        adminClientAuth(jwtService)
        configureMobileAuthority(app)

    }

//    authentication {
//        configureAppAuthority(appConfig)
//
//        jwt {
//            realm = config.realm
//            verifier(
//                JWT
//                    .require(Algorithm.HMAC256(config.secret))
//                    .withAudience(config.audience)
//                    .withIssuer(config.issuer)
//                    .build()
//            )
//            validate { credential ->
//                if (credential.payload.audience.contains(config.audience)) JWTPrincipal(credential.payload) else null
//            }
//        }
//    }
}
