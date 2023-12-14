package com.example.config

import org.koin.core.annotation.Singleton
import io.ktor.server.config.*

@Singleton
class AppConfig {
    val applicationConfiguration: ApplicationConfig = ApplicationConfig("application.conf")
}