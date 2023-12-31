package com.example.route

import com.example.data.about_us.AboutUsDataSource
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
import com.example.route.client_user_side.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.configureMobileClient() {

    authenticate("mobile") {
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