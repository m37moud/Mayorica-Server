package com.example.route

import com.example.route.client_user_side.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.configureMobileClient() {

    authenticate("client") {
        userOrderRequest()
        getUserOrderClient()
        getNearlyProvider()
        productUserRoute()
        categoriesUserRoute()
        /**
         * about us
         */
        aboutUsUserRoute()
        /**
         * hot release app
         */
        hotReleaseUserRoute()
        contactUsUserRoute()
        newsUserRoute()
        offersUserRoute()
        youtubeLinkUserRoute()
    }


}