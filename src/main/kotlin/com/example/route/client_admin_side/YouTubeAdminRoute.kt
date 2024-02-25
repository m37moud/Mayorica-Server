package com.example.route.client_admin_side

import com.example.data.videos.youtube.YoutubeDataSource
import com.example.mapper.toModel
import com.example.models.YoutubeLink
import com.example.models.dto.VideoLinkCreateDto
import com.example.models.request.YoutubeLinkRequest
import com.example.service.storage.StorageService
import com.example.utils.*
import com.example.utils.Constants.ADMIN_CLIENT
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import org.koin.ktor.ext.inject


private const val YOUTUBE_LINKS = "$ADMIN_CLIENT/youtube-links"
private const val YOUTUBE_LINK = "$ADMIN_CLIENT/youtube-link"
private const val CREATE_YOUTUBE_LINK = "$YOUTUBE_LINK/create"
private const val UPDATE_YOUTUBE_LINK = "$YOUTUBE_LINK/update"
private const val DELETE_YOUTUBE_LINK = "$YOUTUBE_LINK/delete"

private val logger = KotlinLogging.logger { }

fun Route.youtubeLinkAdminRoute() {
    val youtubeDataSource: YoutubeDataSource by inject()

    authenticate {

        //get all -> api/v1/admin-client/youtube-links
        get(YOUTUBE_LINKS) {
            logger.debug { "get all youtube Links $YOUTUBE_LINKS" }
            try {
                val linkList = youtubeDataSource.getAllYoutubeVideoLinks()
                if (linkList.isNotEmpty()) {
                    logger.debug { "get all youtube Links $linkList" }

                    respondWithSuccessfullyResult(
                        message = "get all youtube link successfully",
                        result = linkList
                    )

                } else {
                    throw NotFoundException("no Videos link is found")
                }
            } catch (e: Exception) {
                logger.error { "get all youtube Links error ${e.stackTrace}" }
                throw UnknownErrorException(e.message ?: "An unknown error occurred  ")

            }
        }
        //get all -> api/v1/admin-client/youtube-links/{id}
        get("$YOUTUBE_LINK/{id}") {
            logger.debug { "get youtube Links $YOUTUBE_LINK/{id}" }
            call.parameters["id"]?.toIntOrNull()?.let { id ->
                try {
                    val link = youtubeDataSource.getSingleYoutubeVideoLinks(id = id)
                    if (link != null) {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = MyResponse(
                                success = true,
                                message = "get youtube link successfully",
                                data = link
                            )
                        )

                    } else {
                        call.respond(
                            status = HttpStatusCode.NotFound,
                            message = MyResponse(
                                success = false,
                                message = "youtube link is not found",
                                data = null
                            )
                        )


                    }
                } catch (e: Exception) {
                    logger.error { "get youtube Link error ${e.stackTrace}" }
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = MyResponse(
                            success = false,
                            message = e.message ?: "failed to get ",
                            data = null
                        )
                    )


                }

            } ?: call.respond(
                HttpStatusCode.BadRequest,
                MyResponse(
                    success = false,
                    message = "Missing parameters .",
                    data = null
                )
            )

        }
        //post  -> api/v1/admin-client/youtube-links/create
        post(CREATE_YOUTUBE_LINK) {
            logger.debug { "post  youtube Link $CREATE_YOUTUBE_LINK" }

            try {
                val linkRequest = call.receive<VideoLinkCreateDto>()
                val adminUserId = extractAdminId()

                val result = youtubeDataSource.addYoutubeLink(linkRequest.toModel(adminUserId))
                respondWithSuccessfullyResult(
                    result = result,
                    message = "Video Link Information inserted successfully ."
                )


            } catch (e: Exception) {
                logger.error { "$CREATE_YOUTUBE_LINK error ${e.stackTrace ?: "An unknown error occurred"}" }
                throw UnknownErrorException(e.message ?: "An unknown error occurred  ")

            }
        }
        //put  -> api/v1/admin-client/youtube-links/update
        put("$UPDATE_YOUTUBE_LINK/{id}") {
            logger.debug { "put Youtube Link $UPDATE_YOUTUBE_LINK" }

            call.parameters["id"]?.toIntOrNull()?.let {
                val linkRequest = try {
                    call.receive<YoutubeLink>()
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.Conflict,
                        MyResponse(
                            success = false,
                            message = "Missing Some Fields",
                            data = null
                        )
                    )
                    return@put
                }
                val principal = call.principal<JWTPrincipal>()
                val userId = try {
                    principal?.getClaim("userId", String::class)?.toIntOrNull()
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.Conflict,
                        MyResponse(
                            success = false,
                            message = e.message ?: "Failed ",
                            data = null
                        )
                    )
                    return@put
                }
                try {
                    val result = youtubeDataSource.updateYoutubeLink(linkRequest.copy(userAdminId = userId!!))
                    if (result > 0) {
                        call.respond(
                            HttpStatusCode.OK,
                            MyResponse(
                                success = true,
                                message = " Youtube Link Information update successfully .",
                                data = null
                            )
                        )
                        return@put
                    } else {
                        call.respond(
                            HttpStatusCode.OK, MyResponse(
                                success = false,
                                message = "Youtube Link Information update failed .",
                                data = null
                            )
                        )
                        return@put
                    }


                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.Conflict,
                        MyResponse(
                            success = false,
                            message = e.message ?: "error while update ",
                            data = null
                        )
                    )
                    return@put
                }
            } ?: call.respond(
                HttpStatusCode.BadRequest,
                MyResponse(
                    success = false,
                    message = "Missing parameters .",
                    data = null
                )
            )


        }
        //delete  -> api/v1/admin-client/youtube-links/delete
        delete("$DELETE_YOUTUBE_LINK/{id}") {
            logger.debug { "delete Youtube Link $DELETE_YOUTUBE_LINK" }
            try {
                call.parameters["id"]?.toIntOrNull()?.let { id ->

                    val result = youtubeDataSource.deleteYoutubeLink(youtubeId = id)
                    if (result > 0) {
                        respondWithSuccessfullyResult(
                            result = true,
                            message = "Video Link Information delete successfully ."
                        )

                    } else {
                        throw UnknownErrorException("Video Link Information delete failed .")

                    }
                } ?: throw MissingParameterException("Missing parameters .")
            } catch (exc: Exception) {
                logger.error { "$DELETE_YOUTUBE_LINK error ${exc.stackTrace ?: "An unknown error occurred"}" }
                throw UnknownErrorException(exc.message ?: "An Known Error Occurred .")
            }


        }
    }

}