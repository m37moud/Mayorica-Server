package com.example.plugins

import com.example.data.admin_user.UserDataSource
import com.example.data.ceramic_provider.CeramicProviderDataSource
import com.example.data.order.OrderDataSource
import com.example.data.order.OrderStatusDataSource
import com.example.route.client_admin_side.*
import com.example.route.client_user_side.getNearlyProvider
import com.example.route.client_user_side.getUserOrderClient
import com.example.route.client_user_side.userOrderRequest
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
    orderDataSource: OrderDataSource,
    orderStatusDataSource: OrderStatusDataSource,
    ceramicProvider: CeramicProviderDataSource,
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
        userOrderRequest(
            orderDataSource = orderDataSource,
            orderStatusDataSource = orderStatusDataSource
        )
        getUserOrderClient(
            orderDataSource = orderDataSource,
            orderStatusDataSource = orderStatusDataSource,
        )
        orders(
            orderDataSource = orderDataSource,
            orderStatusDataSource = orderStatusDataSource,
            userDataSource = userDataSource
        )
        providerAdminClient(ceramicProvider = ceramicProvider)
        getNearlyProvider(ceramicProvider = ceramicProvider)
        get("/") {
            call.respondText("\uD83D\uDC4B Hello Mayorca Reactive API REST!")
        }
        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }
    }
}
