package com.example.route.client_user_side

import com.example.data.videos.youtube.YoutubeDataSource
import com.example.utils.Constants
import com.example.utils.Constants.USER_CLIENT
import com.example.utils.MyResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging


private const val YOUTUBE_LINKS = "${USER_CLIENT}/youtube-links"
private const val YOUTUBE_LINK = "${USER_CLIENT}/youtube-link"

private val logger = KotlinLogging.logger { }

fun Route.youtubeLinkUserRoute(
    youtubeDataSource: YoutubeDataSource
) {
    //get all -> api/v1/user-client/youtube-links
    get(YOUTUBE_LINKS) {
        logger.debug { "get all youtube Links $YOUTUBE_LINKS" }
        try {
            val linkList = youtubeDataSource.getAllEnabledYoutubeVideoLinks()
            if (linkList.isNotEmpty()) {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = MyResponse(
                        success = true,
                        message = "get all youtube link successfully",
                        data = linkList
                    )
                )

            } else {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = MyResponse(
                        success = false,
                        message = "no youtube link is found",
                        data = null
                    )
                )


            }
        } catch (e: Exception) {
            logger.error { "get all youtube Links error ${e.stackTrace}" }
            call.respond(
                status = HttpStatusCode.Conflict, message = MyResponse(
                    success = false, message = e.message ?: "failed",
                    data = null
                )
            )


        }
    }

}