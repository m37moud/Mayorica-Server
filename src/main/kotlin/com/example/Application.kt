package com.example

import com.example.plugins.*
import com.example.security.token.TokenConfig
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*

//fun main() {
//    embeddedServer(Netty, port = 8081, host = "0.0.0.0", module = Application::module)
//        .start(wait = true)
//}

/**
 * Configure our application with the plugins
 */
@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)
//connector {
//    // Replace with your actual certificate and key paths
//    keyStore = file("/path/to/your/server.keystore")
//    keyStorePassword = "your_keystore_password"
//}
fun Application.module() {
    configureKoin() // Configure the Koin plugin to inject dependencies
    val appConfig = HoconApplicationConfig(ConfigFactory.load())

    val config = TokenConfig(
        audience = appConfig.property("jwt.audience").getString(),
        issuer = appConfig.property("jwt.issuer").getString(),
        expireIn = 360L * 24L * 60L * 60L,
        refreshIn = 360L * 24L * 60L * 60L,
        secret = appConfig.property("jwt.secret").getString(),
        realm = appConfig.property("jwt.realm").getString()
//        secret = System.getenv("JWT_SECRET")
    )

    configureSerialization()
    configureMonitoring()
    configureSecurity(
//        config = config, appConfig = appConfig, app = mobileApp
    )
    configureRouting(
//        config = config
    )
    configureStatusPages()
}
