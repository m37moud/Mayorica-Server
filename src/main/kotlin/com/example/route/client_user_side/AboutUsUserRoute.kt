package com.example.route.client_user_side

import com.example.data.about_us.AboutUsDataSource
import com.example.mapper.toUSerResponse
import com.example.utils.Constants
import com.example.utils.Constants.USER_CLIENT
import com.example.utils.MyResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import org.koin.ktor.ext.inject

private const val ABOUT_US = "${USER_CLIENT}/about-us"
private val logger = KotlinLogging.logger {}

fun Route.aboutUsUserRoute() {
    val aboutUsDataSource: AboutUsDataSource by inject()
//get about us //api/v1/user-client/about_us
    get(ABOUT_US) {
        logger.debug { "GET ABOUT US $ABOUT_US" }
        try {

            val result = aboutUsDataSource.getAllAboutUsInfo()
            if (result.isNotEmpty()) {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = MyResponse(
                        success = true,
                        message = "About Us Information Found",
                        data = result.toUSerResponse()
                    )
                )
            } else {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = MyResponse(
                        success = false,
                        message = "About Us Information Not Found",
                        data = null
                    )
                )
            }
        } catch (exc: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                MyResponse(
                    success = false,
                    message = exc.message ?: "Failed ",
                    data = null
                )
            )
            return@get
        }
    }
}
