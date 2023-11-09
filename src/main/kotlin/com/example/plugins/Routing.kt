package com.example.plugins

import com.example.data.about_us.AboutUsDataSource
import com.example.data.admin_user.UserDataSource
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
import com.example.route.client_user_side.*
import com.example.security.hash.HashingService
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import com.example.service.storage.StorageService
import com.example.utils.Constants.ENDPOINT
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
    productDataSource: ProductDataSource,
    categoryDataSource: CategoryDataSource,
    aboutUsDataSource: AboutUsDataSource,
    hotReleaseDataSource: HotReleaseDataSource,
    contactUsDataSource: ContactUsDataSource,
    newsDataSource: NewsDataSource,
    offersDataSource: OffersDataSource,
    youtubeDataSource: YoutubeDataSource,
    storageService: StorageService,
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
        ordersAdminRoute(
            orderDataSource = orderDataSource,
            orderStatusDataSource = orderStatusDataSource,
            userDataSource = userDataSource
        )
        providerAdminClient(ceramicProvider = ceramicProvider)
        getNearlyProvider(ceramicProvider = ceramicProvider)

        //product
        productAdminRoute(
            productDataSource = productDataSource,
            storageService = storageService
        )
        productUserRoute(
            productDataSource = productDataSource,
        )
        categoriesAdminRoute(
            categoryDataSource = categoryDataSource,
            storageService = storageService
        )
        categoriesUserRoute(
            categoryDataSource = categoryDataSource,

            )
        /**
         * about us
         */
        aboutUsAdminRoute(
            aboutUsDataSource = aboutUsDataSource,

            )
        aboutUsUserRoute(
            aboutUsDataSource = aboutUsDataSource,

            )
        /**
         * hor release app
         */
        hotReleaseAdminRoute(
            hotReleaseDataSource = hotReleaseDataSource
        )
        hotReleaseAdminRoute(
            hotReleaseDataSource = hotReleaseDataSource,
            productDataSource = productDataSource
        )

        contactUsAdminRoute(
            contactUsDataSource = contactUsDataSource
        )
        contactUsUserRoute(
            contactUsDataSource = contactUsDataSource
        )
        newsAdminRoute(
            newsDataSource = newsDataSource,
            storageService = storageService
        )
        newsUserRoute(
            newsDataSource = newsDataSource,
        )
        offersAdminRoute(
            offersDataSource = offersDataSource,
            storageService = storageService
        )
        offersUserRoute(
            offersDataSource = offersDataSource,
        )
        youtubeLinkAdminRoute(
            youtubeDataSource=youtubeDataSource
        )
        youtubeLinkUserRoute(
            youtubeDataSource=youtubeDataSource
        )



        get("/") {
            call.respondText("\uD83D\uDC4B Hello Mayorca Reactive API REST!")
        }
        // Static plugin. Try to access `/static/index.html`
        static("/") {
            resources("static")
            // the path the client will use to access files: /images
            static("$ENDPOINT/image/products") {

                // serve all files in fruit_pictures as static content under /images
                files("uploads/products")
            }
            static("$ENDPOINT/image/news") {

                // serve all files in fruit_pictures as static content under /images
                files("uploads/news")
            }
            static("$ENDPOINT/image/offers") {

                // serve all files in fruit_pictures as static content under /images
                files("uploads/offers")
            }
            static("$ENDPOINT/image/categories/icons") {

                // serve all files in fruit_pictures as static content under /images
                files("uploads/categories/icons")
            }
            static("$ENDPOINT/image/categories/images") {

                // serve all files in fruit_pictures as static content under /images
                files("uploads/categories/images")
            }
        }
    }
}
