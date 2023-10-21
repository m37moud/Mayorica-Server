package com.example

import com.example.data.admin_user.MYSqlUserDataSource
import com.example.data.ceramic_provider.MySqlCeramicProviderDataSource
import com.example.data.order.MYSqlOrderDataSource
import com.example.data.order.MYSqlOrderStatusDataSource
import com.example.database.Database
import com.example.plugins.*
import com.example.security.hash.SHA256HashingService
import com.example.security.token.JWTTokenService
import com.example.security.token.TokenConfig
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8081, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val appConfig = HoconApplicationConfig(ConfigFactory.load())
    val db = Database.db
    val userDataSource = MYSqlUserDataSource(db = db)
    val orderDataSource = MYSqlOrderDataSource(db = db)
    val orderStatusDataSource = MYSqlOrderStatusDataSource(db = db)
    val ceramicProviderDataSource = MySqlCeramicProviderDataSource(db = db)
    val hashingService = SHA256HashingService()
    val tokenService = JWTTokenService()


    val config = TokenConfig(
        audience = appConfig.property("jwt.audience").getString(),
        issuer = appConfig.property("jwt.issuer").getString(),
        expireIn = 360L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )

    configureSerialization()
    configureMonitoring()
    configureSecurity(config = config)
    configureRouting(
        userDataSource = userDataSource,
        orderDataSource=orderDataSource,
        orderStatusDataSource=orderStatusDataSource,
        hashingService = hashingService,
        tokenService = tokenService,
        config = config
    )
}
