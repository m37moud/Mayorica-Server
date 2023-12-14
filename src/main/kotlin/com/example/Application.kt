package com.example

import com.example.data.about_us.MySqlAboutUsDataSource
import com.example.data.administrations.admin_user.MYSqlUserDataSource
import com.example.data.administrations.apps.MySqlMobileAppDataSource
import com.example.data.ceramic_provider.MySqlCeramicProviderDataSource
import com.example.data.contact_us.MySqlContactUsDataSource
import com.example.data.gallery.categories.MySqlCategoryDataSource
import com.example.data.gallery.products.MySqlProductDataSource
import com.example.data.gallery.products.hot_release.MySqlHotReleaseDataSource
import com.example.data.news.MySqlNewsDataSource
import com.example.data.offers.MySqlOffersDataSource
import com.example.data.order.MYSqlOrderDataSource
import com.example.data.order.MYSqlOrderStatusDataSource
import com.example.data.videos.youtube.MySqlYoutubeDataSource
import com.example.database.DBHelper
import com.example.plugins.*
import com.example.security.hash.SHA256HashingService
import com.example.security.token.JWTTokenService
import com.example.security.token.TokenConfig
import com.example.service.storage.StorageServiceImpl
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*

//fun main() {
//    embeddedServer(Netty, port = 8081, host = "0.0.0.0", module = Application::module)
//        .start(wait = true)
//}
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    configureKoin() // Configure the Koin plugin to inject dependencies
    val appConfig = HoconApplicationConfig(ConfigFactory.load())
//    val database = DBHelper(
//        appConfig = appConfig,
//    )

//    val db = database.init()
//    val userDataSource = MYSqlUserDataSource(db = db)
//    val orderDataSource = MYSqlOrderDataSource(db = db)
//    val orderStatusDataSource = MYSqlOrderStatusDataSource(db = db)
//    val ceramicProvider = MySqlCeramicProviderDataSource(db = db)
//    val productDataSource = MySqlProductDataSource(db = db)
//    val categoryDataSource = MySqlCategoryDataSource(db = db)
//    val aboutUsDataSource = MySqlAboutUsDataSource(db = db)
//    val hotReleaseDataSource = MySqlHotReleaseDataSource(db = db)
//    val contactUsDataSource = MySqlContactUsDataSource(db = db)
//    val newsDataSource = MySqlNewsDataSource(db = db)
//    val offersDataSource = MySqlOffersDataSource(db = db)
//    val youtubeDataSource = MySqlYoutubeDataSource(db = db)
//    val mobileApp = MySqlMobileAppDataSource(db = db)
//    val storageService = StorageServiceImpl(appConfig)
//    val hashingService = SHA256HashingService()
//    val tokenService = JWTTokenService()


    val config = TokenConfig(
        audience = appConfig.property("jwt.audience").getString(),
        issuer = appConfig.property("jwt.issuer").getString(),
        expireIn = 360L * 60L * 60L * 24L,
        secret = appConfig.property("jwt.secret").getString(),
        realm = appConfig.property("jwt.realm").getString()
//        secret = System.getenv("JWT_SECRET")
    )

    configureSerialization()
    configureMonitoring()
    configureSecurity(config = config, appConfig = appConfig, app = mobileApp)
    configureRouting(
        userDataSource = userDataSource,
        orderDataSource = orderDataSource,
        orderStatusDataSource = orderStatusDataSource,
        ceramicProvider = ceramicProvider,
        productDataSource = productDataSource,
        categoryDataSource = categoryDataSource,
        aboutUsDataSource = aboutUsDataSource,
        hotReleaseDataSource = hotReleaseDataSource,
        contactUsDataSource = contactUsDataSource,
        newsDataSource = newsDataSource,
        offersDataSource = offersDataSource,
        youtubeDataSource = youtubeDataSource,
        storageService = storageService,
        hashingService = hashingService,
        tokenService = tokenService,
        config = config
    )
}
