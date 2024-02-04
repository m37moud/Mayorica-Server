package com.example.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.config.AppConfig
import mu.KotlinLogging
import org.koin.core.annotation.Single
import java.util.*
import com.auth0.jwt.JWTVerifier
import com.example.models.response.UserTokensResponse
import com.example.utils.Claim.TOKEN_TYPE
import com.example.utils.TokenType
import org.koin.core.annotation.Singleton

private val logger = KotlinLogging.logger {}

@Singleton
class JWTTokenService(
    private val config: AppConfig
) : TokenService {

    override val audience by lazy {
        config.applicationConfiguration.propertyOrNull("jwt.audience")?.getString() ?: ""
    }
    override val realm by lazy {
        config.applicationConfiguration.propertyOrNull("jwt.realm")?.getString() ?: ""
    }
    override val issuer by lazy {
        config.applicationConfiguration.propertyOrNull("jwt.issuer")?.getString() ?: ""
    }
    override val expiresIn by lazy {
        360L * 60L * 60L * 24L * 1000L
//        config.applicationConfiguration.propertyOrNull("jwt.tiempo")?.getString()?.toLong() ?: 3600
    }
    override val refreshIn by lazy {
        360L * 60L * 60L * 24L * 1000L
//        config.applicationConfiguration.propertyOrNull("jwt.tiempo")?.getString()?.toLong() ?: 3600
    }
    override val secret by lazy {
        config.applicationConfiguration.propertyOrNull("jwt.secret")?.getString() ?: "jwt-secret"
    }
    override val appApiKey by lazy {
        config.applicationConfiguration.propertyOrNull("ktor.appAuth.apiKey")?.getString() ?: ""

    }

    override val isProductionServer by lazy {
        config.applicationConfiguration.propertyOrNull("ktor.productionServer")?.getString()?.toBoolean() ?: false

    }



    init {
        logger.debug {
            "Init tokens service with" +
                    "\n audience: $audience" +
                    " \n issuer : $issuer" +
                    " \n secret : $secret " +
                    "\n realm : $realm" +
                    "\n appApiKey : $appApiKey" +
                    "\n isProductionServer : $isProductionServer"

        }
    }

    override fun generateUserTokens(
        vararg claim: TokenClaim
    ): UserTokensResponse {

        val accessTokenExpirationDate = getExpirationDate(expiresIn)
        val refreshTokenExpirationDate = getExpirationDate(refreshIn)

        val refreshToken =
            generateToken(
                tokenType = TokenType.REFRESH_TOKEN,
                claims = claim
            )
        val accessToken =
            generateToken(
                tokenType = TokenType.ACCESS_TOKEN,
                claims = claim)

        return UserTokensResponse(
            accessTokenExpirationDate.time,
            refreshTokenExpirationDate.time,
            accessToken,
            refreshToken
        )
    }

    override fun generateToken(
//        config: TokenConfig,
        tokenType: TokenType,
        vararg claims: TokenClaim
    ): String {
        var token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + expiresIn))
            .withClaim(TOKEN_TYPE, tokenType.name)
        claims.forEach { claim ->
            token = token.withClaim(claim.name, claim.value)
        }

        return token.sign(Algorithm.HMAC256(secret))
    }

    private fun getExpirationDate(timestamp: Long): Date {
        return Date(System.currentTimeMillis() + timestamp)
    }


    /**
     * Verify a token JWT
     * @return JWTVerifier
     * @throws TokenException.InvalidTokenException
     */
    override fun verifyJWT(): JWTVerifier? {
        logger.debug {
            "verifyJWT tokens service with" +
                    "\n audience: $audience" +
                    " \n issuer : $issuer" +
                    " \n secret : $secret " +
                    "\n realm : $realm" +
                    "\n appApiKey : $appApiKey"
        }


        return try {
            JWT.require(Algorithm.HMAC256(secret))
                .withAudience(audience)
                .withIssuer(issuer)
                .build()
        } catch (e: Exception) {
            logger.debug { "jwt error ${e.message}" }
            null
        }
    }
}