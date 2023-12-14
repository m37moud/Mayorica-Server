package com.example.plugins
import com.example.di.AppModule
import io.ktor.server.application.*
import org.koin.ksp.generated.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger() // Logger
//        defaultModule() // Default module with Annotations
         modules(AppModule().module) // Our module, without dependencies
    }
}