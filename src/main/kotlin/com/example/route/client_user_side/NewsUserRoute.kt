package com.example.route.client_user_side

import com.example.data.news.NewsDataSource
import com.example.mapper.toUserResponse
import com.example.utils.Constants
import com.example.utils.Constants.USER_CLIENT
import com.example.utils.MyResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import org.koin.ktor.ext.inject

private const val NEWS = "${USER_CLIENT}/news"

private val logger = KotlinLogging.logger { }

fun Route.newsUserRoute() {
    val newsDataSource: NewsDataSource by inject()
    //get all -> api/v1/user-client/news
    get(NEWS) {
        logger.debug { "get all News  $NEWS" }
        try {
            val linkList = newsDataSource.getAllNews()
            if (linkList.isNotEmpty()) {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = MyResponse(
                        success = true,
                        message = "get all News  successfully",
                        data = linkList.toUserResponse()
                    )
                )

            } else {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = MyResponse(
                        success = false,
                        message = "No News Is Found",
                        data = null
                    )
                )


            }
        } catch (e: Exception) {
            logger.error { "get all News error ${e.stackTrace}" }
            call.respond(
                status = HttpStatusCode.Conflict, message = MyResponse(
                    success = false, message = e.message ?: "failed",
                    data = null
                )
            )


        }
    }

}