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
//    userDataSource: UserDataSource,
//    orderDataSource: OrderDataSource,
//    orderStatusDataSource: OrderStatusDataSource,
//    ceramicProvider: CeramicProviderDataSource,
//    productDataSource: ProductDataSource,
//    categoryDataSource: CategoryDataSource,
//    aboutUsDataSource: AboutUsDataSource,
//    hotReleaseDataSource: HotReleaseDataSource,
//    contactUsDataSource: ContactUsDataSource,
//    newsDataSource: NewsDataSource,
//    offersDataSource: OffersDataSource,
//    youtubeDataSource: YoutubeDataSource,
//    storageService: StorageService,
//    hashingService: HashingService,
//    tokenService: TokenService,
//    config: TokenConfig
) {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        registerAdmin()
        appsAdminRoute()
        configureDashboardClient(
//            userDataSource = userDataSource,
//            orderDataSource = orderDataSource,
//            orderStatusDataSource = orderStatusDataSource,
//            ceramicProvider = ceramicProvider,
//            productDataSource = productDataSource,
//            categoryDataSource = categoryDataSource,
//            aboutUsDataSource = aboutUsDataSource,
//            hotReleaseDataSource = hotReleaseDataSource,
//            newsDataSource = newsDataSource,
//            contactUsDataSource = contactUsDataSource,
//            offersDataSource = offersDataSource,
//            youtubeDataSource=youtubeDataSource,
//            storageService = storageService,
//            hashingService = hashingService,
//            tokenService = tokenService,
//            config = config,
        )
        configureMobileClient()

//        adminUsers(
//            userDataSource = userDataSource,
//
//            )
//        login(
//            userDataSource = userDataSource,
//            hashingService = hashingService,
//            tokenService = tokenService,
//            config = config
//        )
//        getSecretInfo()
//        register(
//            userDataSource = userDataSource,
//            hashingService = hashingService
//        )
//        userOrderRequest(
//            orderDataSource = orderDataSource,
//            orderStatusDataSource = orderStatusDataSource
//        )
//        getUserOrderClient(
//            orderDataSource = orderDataSource,
//            orderStatusDataSource = orderStatusDataSource,
//        )
//        ordersAdminRoute(
//            orderDataSource = orderDataSource,
//            orderStatusDataSource = orderStatusDataSource,
//            userDataSource = userDataSource
//        )
//        providerAdminClient(ceramicProvider = ceramicProvider)
//        getNearlyProvider(ceramicProvider = ceramicProvider)

        //product
//        productAdminRoute(
//            productDataSource = productDataSource,
//            storageService = storageService
//        )
//        productUserRoute(
//            productDataSource = productDataSource,
//        )
//        categoriesAdminRoute(
//            categoryDataSource = categoryDataSource,
//            storageService = storageService
//        )
//        categoriesUserRoute(
//            categoryDataSource = categoryDataSource,
//
//            )
//        /**
//         * about us
//         */
//        aboutUsAdminRoute(
//            aboutUsDataSource = aboutUsDataSource,
//
//            )
//        aboutUsUserRoute(
//            aboutUsDataSource = aboutUsDataSource,
//
//            )
//        /**
//         * hot release app
//         */

//        hotReleaseUserRoute(
//            hotReleaseDataSource = hotReleaseDataSource
//        )
//        hotReleaseAdminRoute(
//            hotReleaseDataSource = hotReleaseDataSource,
//            productDataSource = productDataSource
//        )

//        contactUsAdminRoute(
//            contactUsDataSource = contactUsDataSource
//        )
//        contactUsUserRoute(
//            contactUsDataSource = contactUsDataSource
//        )
//        newsAdminRoute(
//            newsDataSource = newsDataSource,
//            storageService = storageService
//        )
//        newsUserRoute(
//            newsDataSource = newsDataSource,
//        )
//        offersAdminRoute(
//            offersDataSource = offersDataSource,
//            storageService = storageService
//        )
//        offersUserRoute(
//            offersDataSource = offersDataSource,
//        )
//        youtubeLinkAdminRoute(
//            youtubeDataSource=youtubeDataSource
//        )
//        youtubeLinkUserRoute(
//            youtubeDataSource=youtubeDataSource
//        )



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
//        static("/") {
//            resources("static")
//            // the path the client will use to access files: /images
//            static("$ENDPOINT/image/products") {
//
//                // serve all files in fruit_pictures as static content under /images
//                files("uploads/products")
//            }
//            static("$ENDPOINT/image/news") {
//
//                // serve all files in fruit_pictures as static content under /images
//                files("uploads/news")
//            }
//            static("$ENDPOINT/image/offers") {
//
//                // serve all files in fruit_pictures as static content under /images
//                files("uploads/offers")
//            }
//            static("$ENDPOINT/image/categories/icons") {
//
//                // serve all files in fruit_pictures as static content under /images
//                files("uploads/categories/icons")
//            }
//            static("$ENDPOINT/image/categories/images") {
//
//                // serve all files in fruit_pictures as static content under /images
//                files("uploads/categories/images")
//            }
//        }
    }
}
