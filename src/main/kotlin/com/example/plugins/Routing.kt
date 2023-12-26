package com.example.plugins

import com.example.route.auth_route.registerAdmin
import com.example.route.client_admin_side.*
import com.example.route.configureDashboardClient
import com.example.route.configureMobileClient
import com.example.utils.Constants.ENDPOINT
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRouting(
//    config: TokenConfig
) {

    routing {
        registerAdmin()
        appsAdminRoute()
        configureDashboardClient(
//            config = config,
        )
        configureMobileClient()


        get("/") {
            call.respondText("\uD83D\uDC4B Hello Mayorca Reactive API REST!")
        }
        // Static plugin. Try to access `/static/index.html`
        staticFiles("/" , File("static"))
        staticFiles("/$ENDPOINT/image/products" , File("uploads/products"))
        staticFiles("/$ENDPOINT/image/news" , File("uploads/news"))
        staticFiles("/$ENDPOINT/image/offers" , File("uploads/offers"))
        staticFiles("/$ENDPOINT/image/categories/icons" , File("uploads/categories/icons"))
        staticFiles("/$ENDPOINT/image/categories/images" , File("uploads/categories/images"))
    }
}
