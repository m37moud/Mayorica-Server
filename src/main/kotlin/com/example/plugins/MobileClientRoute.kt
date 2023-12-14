package com.example.plugins

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
import io.ktor.server.routing.*

fun Route.configureMobileClient(
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


    ) {


    userOrderRequest(
//        orderDataSource = orderDataSource,
//        orderStatusDataSource = orderStatusDataSource
    )
    getUserOrderClient(
//        orderDataSource = orderDataSource,
//        orderStatusDataSource = orderStatusDataSource,
    )
    getNearlyProvider(
//        ceramicProvider = ceramicProvider
    )
    productUserRoute(
//        productDataSource = productDataSource,
    )
    categoriesUserRoute(
//        categoryDataSource = categoryDataSource,

        )
    /**
     * about us
     */
    aboutUsUserRoute(
//        aboutUsDataSource = aboutUsDataSource,

        )

    /**
     * hot release app
     */

    hotReleaseUserRoute(
//        hotReleaseDataSource = hotReleaseDataSource
    )
    contactUsUserRoute(
//        contactUsDataSource = contactUsDataSource
    )
    newsUserRoute(
//        newsDataSource = newsDataSource,
    )
    offersUserRoute(
//        offersDataSource = offersDataSource,
    )
    youtubeLinkUserRoute(
//        youtubeDataSource=youtubeDataSource
    )
}