package com.example.route.client_admin_side

import com.example.data.videos.youtube.YoutubeDataSource
import com.example.utils.Constants.ADMIN_CLIENT
import com.example.utils.MyResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging


private const val YOUTUBE_LINKS = "$ADMIN_CLIENT/youtube-links"
private const val YOUTUBE_LINK = "$ADMIN_CLIENT/youtube-link"
private const val CREATE_YOUTUBE_LINK = "$YOUTUBE_LINK/create"
private const val UPDATE_YOUTUBE_LINK = "$YOUTUBE_LINK/delete"
private const val DELETE_YOUTUBE_LINK = "$YOUTUBE_LINK/update"

private val logger = KotlinLogging.logger { }

fun Route.youtubeLinkAdminRoute(
    youtubeDataSource: YoutubeDataSource
) {
authenticate {

    //get all -> api/v1/admin-client/youtube-links
    get(YOUTUBE_LINKS) {
        logger.debug { "get all youtube Links $YOUTUBE_LINKS" }
        try {
            val linkList = youtubeDataSource.getAllYoutubeVideoLinks()
            if (linkList.isNotEmpty()) {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = MyResponse(
                        success = true,
                        message = "get all youtube link successfully",
                        data = null
                    )
                )

            } else {
                call.respond(
                    status = HttpStatusCode.NotFound,
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
    
    post (CREATE_YOUTUBE_LINK){
        logger.debug { "post  youtube Link $CREATE_YOUTUBE_LINK" }
        // TODO: complete youtube link logic 
        
    }
}

}