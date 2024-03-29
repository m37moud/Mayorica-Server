package com.example.plugins

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.AuthScheme
import io.github.smiley4.ktorswaggerui.dsl.AuthType
import io.ktor.server.application.*

fun Application.configureSwagger() {
    // https://github.com/SMILEY4/ktor-swagger-ui/wiki/Configuration
    // http://xxx/swagger/
    install(SwaggerUI) {
        swagger {
            swaggerUrl = "swagger"
            forwardRoot = false
        }
        info {
            title = "Mayorc Reactive API REST"
            version = "latest"
            description = "Example of a Ktor API REST using Kotlin and Ktor"
            contact {
                name = "Mahmoud Ali"
                url = "https://github.com/m37moud"
            }

        }

        schemasInComponentSection = true
        examplesInComponentSection = true
        automaticTagGenerator = { url -> url.firstOrNull() }
        // We can filter paths and methods
        pathFilter = { method, url ->
            url.contains("ceramic")
            //(method == HttpMethod.Get && url.firstOrNull() == "api")
            // || url.contains("test")
        }

        // We can add security
        securityScheme("JWT-Auth") {
            type = AuthType.HTTP
            scheme = AuthScheme.BEARER
            bearerFormat = "jwt"
        }
    }
}