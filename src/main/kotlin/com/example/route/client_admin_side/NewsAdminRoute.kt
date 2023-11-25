package com.example.route.client_admin_side

import com.example.data.news.NewsDataSource
import com.example.models.News
import com.example.service.storage.StorageService
import com.example.utils.*
import com.example.utils.Constants.ADMIN_CLIENT
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import java.time.LocalDateTime

private const val NEWS = "${ADMIN_CLIENT}/news"
private const val CREATE_NEWS = "$NEWS/create"
private const val UPDATE_NEWS = "$NEWS/update"
private const val DELETE_NEWS = "$NEWS/delete"

private val logger = KotlinLogging.logger { }

fun Route.newsAdminRoute(
    newsDataSource: NewsDataSource,
    storageService: StorageService

) {

    authenticate {
        //get all -> api/v1/admin-client/news
        get(NEWS) {
            logger.debug { "get all NEWS  $NEWS" }
            try {
                val linkList = newsDataSource.getAllNews()
                if (linkList.isNotEmpty()) {
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = MyResponse(
                            success = true,
                            message = "get all NEWS  successfully",
                            data = null
                        )
                    )

                } else {
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = MyResponse(
                            success = false,
                            message = "no NEWS is found",
                            data = null
                        )
                    )


                }
            } catch (e: Exception) {
                logger.error { "get all NEWS error ${e.stackTrace}" }
                call.respond(
                    status = HttpStatusCode.Conflict, message = MyResponse(
                        success = false, message = e.message ?: "failed",
                        data = null
                    )
                )


            }
        }
        //get -> api/v1/admin-client/news/{id}
        get("$NEWS/{id}") {
            logger.debug { "get NEWS $NEWS/{id}" }
            call.parameters["id"]?.toIntOrNull()?.let { id ->
                try {
                    val news = newsDataSource.getNewsById(newsId = id)
                    if (news != null) {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = MyResponse(
                                success = true,
                                message = "get NEWS successfully",
                                data = null
                            )
                        )

                    } else {
                        call.respond(
                            status = HttpStatusCode.NotFound,
                            message = MyResponse(
                                success = false,
                                message = "NEWS is not found",
                                data = null
                            )
                        )


                    }
                } catch (e: Exception) {
                    logger.error { "get NEWS error ${e.stackTrace}" }
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
        // post the NEWS --> POST /api/v1/admin-client/news/create (token required)
        post(CREATE_NEWS) {
            logger.debug { "post NEWS $CREATE_NEWS" }
            val multiPart = call.receiveMultipart()
            var title: String? = null
            var newsDescription: String? = null
            var fileName: String? = null
            var fileBytes: ByteArray? = null
            var url: String? = null
            var imageUrl: String? = null
            val principal = call.principal<JWTPrincipal>()
            val userAdminId = try {
                principal?.getClaim("userId", String::class)?.toIntOrNull()
            } catch (e: Exception) {
//                logger.runCatching { "post /$e" }
                call.respond(
                    status = HttpStatusCode.Conflict, message = MyResponse(
                        success = false,
                        message = e.message ?: "Error with authentication",
                        data = null
                    )
                )
                return@post
            }
            val baseUrl =
                call.request.origin.scheme + "://" + call.request.host() + ":" + call.request.port() + "${Constants.ENDPOINT}/image/"

            multiPart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        // to read parameters that we sent with the image
                        when (part.name) {
                            "title" -> {
                                title = part.value
                            }

                            "newsDescription" -> {
                                newsDescription = part.value
                            }

                        }

                    }

                    is PartData.FileItem -> {
                        val isValid = part.originalFileName as String
                        logger.debug { "isValid $isValid" }
                        if (isValid.isNotEmpty()) {
                            logger.debug { "check if empty $isValid" }

                            if (!isImageContentType(part.contentType.toString())) {
                                call.respond(
                                    message = MyResponse(
                                        success = false,
                                        message = "Invalid file format",
                                        data = null
                                    ), status = HttpStatusCode.BadRequest
                                )
                                part.dispose()
                                return@forEachPart

                            }
                            fileName = generateSafeFileName(part.originalFileName as String)
//                            imageUrl = "$baseUrl$fileName"
                            fileBytes = part.streamProvider().readBytes()
                            url = "${baseUrl}news/${fileName}"
                        } else {
                            fileName = null

                        }


                    }

                    else -> {}

                }
                part.dispose()
            }

            try {
                if (!fileName.isNullOrEmpty()) {
                    logger.debug { "check if empty fileName $fileName" }

                    imageUrl = try {
                        storageService.saveProductImage(
                            fileName = fileName!!,
                            fileUrl = url!!,
                            fileBytes = fileBytes!!
                        )
                    } catch (e: Exception) {
                        storageService.deleteNewsImages(fileName = fileName!!)
                        call.respond(
                            status = HttpStatusCode.InternalServerError,
                            message = MyResponse(
                                success = false,
                                message = e.message ?: "Error happened while uploading Image.",
                                data = null
                            )
                        )
                        return@post
                    }
                }
                logger.debug { "check if not empty title $title newsDescription $newsDescription userAdminId $userAdminId imageUrl $imageUrl" }


                News(
                    title = title!!,
                    newsDescription = newsDescription!!,
                    userAdminId = userAdminId!!,
                    image = imageUrl,
//                    createdAt = LocalDateTime.now().toDatabaseString(),
//                    updatedAt = "",
                ).apply {
                    logger.debug { "check if not empty title $title newsDescription $newsDescription userAdminId $userAdminId imageUrl $imageUrl" }

                    val isInserted = newsDataSource.addNews(this)
                    if (isInserted > 0) {
                        call.respond(
                            status = HttpStatusCode.Created,
                            message = MyResponse(
                                success = true,
                                message = "news inserted successfully",
                                data = this
                            )
                        )
                        return@post
                    } else {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = MyResponse(
                                success = false,
                                message = "news insert failed",
                                data = null
                            )
                        )
                        return@post
                    }
                }
//                if (!imageUrl.isNullOrEmpty()) {
//                    News(
//                        title = title!!,
//                        newsDescription = newsDescription!!,
//                        userAdminId = userAdminId!!,
//                        image = imageUrl ?: "",
//                        createdAt = LocalDateTime.now().toDatabaseString(),
//                        updatedAt = "",
//                    ).apply {
//
//                        val isInserted = newsDataSource.addNews(this)
//                        if (isInserted > 0) {
//                            call.respond(
//                                status = HttpStatusCode.Created,
//                                message = MyResponse(
//                                    success = true,
//                                    message = "news inserted successfully",
//                                    data = this
//                                )
//                            )
//                            return@post
//                        } else {
//                            call.respond(
//                                status = HttpStatusCode.OK,
//                                message = MyResponse(
//                                    success = false,
//                                    message = "news insert failed",
//                                    data = null
//                                )
//                            )
//                            return@post
//                        }
//                    }
//
//                } else {
//                    storageService.deleteNewsImages(fileName = fileName!!)
//                    call.respond(
//                        status = HttpStatusCode.Conflict,
//                        message = MyResponse(
//                            success = false,
//                            message = "some Error happened while uploading .",
//                            data = null
//                        )
//                    )
//                    return@post
//                }


            } catch (ex: Exception) {
                // something went wrong with the image part, delete the file
                storageService.deleteNewsImages(fileName = fileName!!)
                ex.printStackTrace()
                call.respond(
                    status = HttpStatusCode.InternalServerError, message = MyResponse(
                        success = false,
                        message = ex.message ?: "Error happened while uploading .",
                        data = null
                    )
                )
                return@post
            }


        }
        // delete the NEWS --> delete /api/v1/admin-client/news/delete/{id} (token required)
        delete("$DELETE_NEWS/{id}") {
            logger.debug { "delete /$DELETE_NEWS" }
            call.parameters["id"]?.toIntOrNull()?.let { id ->
                try {
                    newsDataSource.getNewsById(newsId = id)?.let {
                        val isDeletedImage = try {
                            storageService.deleteNewsImages(fileName = it.image!!.substringAfterLast("/"))
                        } catch (e: Exception) {
                            call.respond(
                                HttpStatusCode.InternalServerError,
                                MyResponse(
                                    success = false,
                                    message = e.message ?: "Failed to delete image",
                                    data = null
                                )
                            )
                            return@delete
                        }
                        if (isDeletedImage) {
                            val deleteResult = newsDataSource.deleteNews(newsId = id)
                            if (deleteResult > 0) {
                                call.respond(
                                    HttpStatusCode.OK,
                                    MyResponse(
                                        success = true,
                                        message = "NEWS deleted successfully .",
                                        data = null
                                    )
                                )
                            } else {
                                call.respond(
                                    HttpStatusCode.NotFound,
                                    MyResponse(
                                        success = false,
                                        message = " NEWS deleted failed .",
                                        data = null
                                    )
                                )
                            }

                        } else {
                            call.respond(
                                HttpStatusCode.Conflict,
                                MyResponse(
                                    success = false,
                                    message = "Failed to delete image",
                                    data = null
                                )
                            )

                        }

                    } ?: call.respond(
                        HttpStatusCode.NotFound,
                        MyResponse(
                            success = false,
                            message = " news deleted failed .",
                            data = null
                        )
                    )

                } catch (e: Exception) {
                    logger.error { "Exception /${e.stackTrace}" }
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = MyResponse(
                            success = false,
                            message = e.message ?: "failed",
                            data = null
                        )
                    )
                    return@delete
                }

            } ?: call.respond(
                status = HttpStatusCode.BadRequest,
                message = MyResponse(
                    success = false,
                    message = "Missing parameters",
                    data = null
                )
            )

        }

        // TODO: handle update logic
    }

}