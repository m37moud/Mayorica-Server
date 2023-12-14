package com.example.plugins

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

fun Route.configureDashboardClient(
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

    ordersAdminRoute(
        orderDataSource = orderDataSource,
        orderStatusDataSource = orderStatusDataSource,
        userDataSource = userDataSource
    )
    providerAdminClient(ceramicProvider = ceramicProvider)
    productAdminRoute(
        productDataSource = productDataSource,
        storageService = storageService
    )
    categoriesAdminRoute(
        categoryDataSource = categoryDataSource,
        storageService = storageService
    )
    /**
     * about us
     */
    aboutUsAdminRoute(
        aboutUsDataSource = aboutUsDataSource,

        )
    /**
     * hot release app
     */
    hotReleaseAdminRoute(
        hotReleaseDataSource = hotReleaseDataSource,
        productDataSource = productDataSource
    )
    contactUsAdminRoute(
        contactUsDataSource = contactUsDataSource
    )
    newsAdminRoute(
        newsDataSource = newsDataSource,
        storageService = storageService
    )
    offersAdminRoute(
        offersDataSource = offersDataSource,
        storageService = storageService
    )
    youtubeLinkAdminRoute(
        youtubeDataSource=youtubeDataSource
    )
}