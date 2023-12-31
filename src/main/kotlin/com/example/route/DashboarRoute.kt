package com.example.route

import com.example.data.about_us.AboutUsDataSource
import com.example.data.administrations.admin_user.UserDataSource
import com.example.data.ceramic_provider.CeramicProviderDataSource
import com.example.data.contact_us.ContactUsDataSource
import com.example.data.gallery.categories.CategoryDataSource
import com.example.data.gallery.products.ProductDataSource
import com.example.data.gallery.products.hot_release.HotReleaseDataSource
import com.example.data.news.NewsDataSource
import com.example.data.offers.OffersDataSource
import com.example.data.order.OrderDataSource
import com.example.data.order.OrderStatusDataSource
import com.example.data.videos.youtube.YoutubeDataSource
import com.example.route.client_admin_side.*
import com.example.security.hash.HashingService
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import com.example.service.storage.StorageService
import io.ktor.server.routing.*

fun Route.configureDashboardClient() {
    authenticationRoutes()

    ordersAdminRoute()
    providerAdminClient()
    productAdminRoute()
    categoriesAdminRoute()
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