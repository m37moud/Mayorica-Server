package com.example.plugins

import com.example.data.admin_user.UserDataSource
import com.example.route.client_admin_side.adminUsers
import com.example.route.client_admin_side.getSecretInfo
import com.example.route.client_admin_side.login
import com.example.route.client_admin_side.register
import com.example.security.hash.HashingService
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    config: TokenConfig
) {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        adminUsers(
            userDataSource = userDataSource,

            )
        login(
            userDataSource = userDataSource,
            hashingService = hashingService,
            tokenService = tokenService,
            config = config
        )
        getSecretInfo()
        register(
            userDataSource = userDataSource,
            hashingService = hashingService
        )
        get("/") {
            call.respondText("Hello World!")
        }
        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }
    }
}
