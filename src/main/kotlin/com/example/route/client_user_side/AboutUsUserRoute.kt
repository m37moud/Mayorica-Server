package com.example.route.client_user_side

import com.example.data.about_us.AboutUsDataSource
import com.example.utils.Constants
import com.example.utils.Constants.USER_CLIENT
import com.example.utils.MyResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging

private const val ABOUT_US = "${USER_CLIENT}/about_us"
private val logger = KotlinLogging.logger {}

fun Route.aboutUsUserRoute(aboutUsDataSource: AboutUsDataSource) {
//get about us //api/v1/user-client/about_us
    get(ABOUT_US) {
       logger.debug { "GET ABOUT US $ABOUT_US" }
        try {

            aboutUsDataSource.getAboutUsInfo()?.let {
                call.respond(
                    status = HttpStatusCode.OK, message = MyResponse(
                        success = true,
                        message = "About Us Information Found",
                        data = it
                    )
                )
            } ?: call.respond(
                status = HttpStatusCode.NotFound, message = MyResponse(
                    success = false,
                    message = "About Us Information Not Found",
                    data = null
                )
            )
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
