package com.example.route

import com.example.route.client_admin_side.*
import io.ktor.server.routing.*

fun Route.configureDashboardClient() {
    authenticationRoutes()

    ordersAdminRoute()
    providerAdminClient()
    productAdminRoute()
    typeCategoryAdminRoute()
    sizeCategoryAdminRoute()
    colorCategoryAdminRoute()
    /**
     * about us
     */
    aboutUsAdminRoute()
    /**
     * hot release app
     */
    hotReleaseAdminRoute()
    contactUsAdminRoute()
    newsAdminRoute()
    offersAdminRoute()
    youtubeLinkAdminRoute()
}