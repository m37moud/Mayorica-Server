package com.example.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.config.AppConfig
import mu.KotlinLogging
import org.koin.core.annotation.Single
import java.util.*
import com.auth0.jwt.JWTVerifier
import org.koin.core.annotation.Singleton

private val logger = KotlinLogging.logger {}

@Singleton
class JWTTokenService(
    private val config: AppConfig
) : TokenService {

    override val audience by lazy {
        config.applicationConfiguration.propertyOrNull("jwt.audience")?.getString() ?: "jwt-audience"
    }
    override val realm by lazy {
        config.applicationConfiguration.propertyOrNull("jwt.realm")?.getString() ?: "jwt-realm"
    }
    override val issuer by lazy {
        config.applicationConfiguration.propertyOrNull("jwt.issuer")?.getString() ?: "jwt-issuer"
    }
    override val expiresIn by lazy {
        360L * 60L * 60L * 24L*1000L
//        config.applicationConfiguration.propertyOrNull("jwt.tiempo")?.getString()?.toLong() ?: 3600
    }
    override val refreshIn by lazy {
        360L * 60L * 60L * 24L*1000L
//        config.applicationConfiguration.propertyOrNull("jwt.tiempo")?.getString()?.toLong() ?: 3600
    }
    override val secret by lazy {
        config.applicationConfiguration.propertyOrNull("jwt.secret")?.getString() ?: "jwt-secret"
    }
    override val appApiKey by lazy {
        config.applicationConfiguration.propertyOrNull("ktor.appAuth.apiKey")?.getString() ?: ""

    }


    init {
        logger.debug { "Init tokens service with audience: $audience" }
    }

    override fun generateToken(
//        config: TokenConfig,
        vararg claims: TokenClaim
    ): String {
        var token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + expiresIn))
        claims.forEach { claim ->
            token = token.withClaim(claim.name, claim.value)
        }


        return token.sign(Algorithm.HMAC256(secret))
    }

    /**
     * Verify a token JWT
     * @return JWTVerifier
     * @throws TokenException.InvalidTokenException
     */
    override fun verifyJWT(): JWTVerifier? {

        return try {
            JWT.require(Algorithm.HMAC256(secret))
                .withAudience(audience)
                .withIssuer(issuer)
                .build()
        } catch (e: Exception) {
            null
        }
    }
}