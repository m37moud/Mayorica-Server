package com.example.route.client_admin_side

import com.example.data.news.NewsDataSource
import com.example.data.offers.OffersDataSource
import com.example.models.Offers
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
import org.koin.ktor.ext.inject
import java.time.LocalDateTime


const val ALL_OFFERS = "${ADMIN_CLIENT}/offers"
const val SINGLE_OFFERS = "${ADMIN_CLIENT}/offer"
const val CREATE_OFFERS = "${SINGLE_OFFERS}/create"
const val UPDATE_OFFERS = "${SINGLE_OFFERS}/update"
const val DELETE_OFFERS = "${SINGLE_OFFERS}/delete"
const val DELETE_ALL_OFFERS = "${SINGLE_OFFERS}/delete-all"


private val logger = KotlinLogging.logger { }


fun Route.offersAdminRoute(
//    offersDataSource: OffersDataSource,
//    storageService: StorageService
) {
    val offersDataSource: OffersDataSource by inject()
    val storageService: StorageService by inject()

    authenticate {
        //get all offers //api/v1/admin-client/offers

        get(ALL_OFFERS) {
            try {

                logger.debug { "GET ALL /$ALL_OFFERS" }

                val result = offersDataSource.getAllOffers()
                if (result.isNotEmpty()) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "get all Offers successfully",
                            data = result
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        MyResponse(
                            success = false,
                            message = "No Available Offers",
                            data = null
                        )
                    )
                }

            } catch (exc: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = exc.message ?: "Transaction Failed ",
                        data = null
                    )
                )
                return@get
            }

        }
        //get offer //api/v1/admin-client/offer/{id}
        get("$SINGLE_OFFERS/{id}") {
            try {
                logger.debug { "get /$SINGLE_OFFERS/{id}" }
                val id = call.parameters["id"]?.toIntOrNull()
                id?.let {
                    offersDataSource.getOffersById(it)?.let { offer ->
                        call.respond(
                            HttpStatusCode.OK,
                            MyResponse(
                                success = true,
                                message = "offer is found .",
                                data = offer
                            )
                        )
                    } ?: call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = false,
                            message = "no offer found .",
                            data = null
                        )
                    )

                } ?: call.respond(
                    HttpStatusCode.BadRequest,
                    MyResponse(
                        success = false,
                        message = "Missing parameters .",
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
        //post type category //api/v1/admin-client/offer/create
        post(CREATE_OFFERS) {
            logger.debug { "POST /$CREATE_OFFERS" }
            val multiPart = call.receiveMultipart()
            var offerTitle: String? = null
            var offerDescription: String? = null
            var offerEndedAt: String? = null
            var isHotOffer: Boolean? = null
            var fileName: String? = null
            var fileBytes: ByteArray? = null
            var url: String? = null
            var imageUrl: String? = null
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
                return@post
            }
            try {

                val baseUrl =
                    call.request.origin.scheme + "://" + call.request.host() + ":" + call.request.port() + "${Constants.ENDPOINT}/image/"
                multiPart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            // to read parameters that we sent with the image
                            when (part.name) {
                                "offerTitle" -> {
                                    offerTitle = part.value
                                }

                                "offerDescription" -> {
                                    offerDescription = part.value
                                }

                                "isHotOffer" -> {
                                    isHotOffer = part.value.toBoolean()
                                }

                                "offerEndedAt" -> {
                                    offerEndedAt = part.value
                                }

                            }

                        }

                        is PartData.FileItem -> {
                            val isValid = part.originalFileName as String
                            if (isValid.isNotEmpty()) {
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
                                fileBytes = part.streamProvider().readBytes()
                                url = "${baseUrl}offers/${fileName}"
                            } else {
                                fileName = null
                            }
                        }

                        else -> {}

                    }
                    part.dispose()
                }

                val typeCategory = offersDataSource.getOfferByTitle(offerTitle!!)
                if (typeCategory == null) {

                    if (!fileName.isNullOrEmpty()) {

                        imageUrl = try {

                            storageService.saveOfferImage(
                                fileName = fileName!!,
                                fileUrl = url!!,
                                fileBytes = fileBytes!!
                            )
                        } catch (e: Exception) {
                            storageService.deleteOfferImages(fileName = fileName!!)
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
                    Offers(
                        title = offerTitle!!,
                        offerDescription = offerDescription!!,
                        image = imageUrl,
                        isHotOffer = isHotOffer!!,
                        userAdminID = userId!!,
                        createdAt = LocalDateTime.now().toDatabaseString(),
                        updatedAt = LocalDateTime.now().toDatabaseString(),
                        endedAt = offerEndedAt!!
                    ).apply {

                        val result = offersDataSource.addOffers(this)

                        if (result > 0) {
                            call.respond(
                                HttpStatusCode.OK, MyResponse(
                                    success = true,
                                    message = "Offer inserted successfully .",
                                    data = this
                                )
                            )
                            return@post
                        } else {
                            call.respond(
                                HttpStatusCode.OK,
                                MyResponse(
                                    success = false,
                                    message = "Offer inserted failed .",
                                    data = null
                                )
                            )
                            return@post
                        }
                    }

//                    if (!imageUrl.isNullOrEmpty()) {
//
//                        Offers(
//                            title = offerTitle!!,
//                            offerDescription = offerDescription!!,
//                            image = imageUrl,
//                            isHotOffer = isHotOffer!!,
//                            userAdminID = userId!!,
//                            createdAt = LocalDateTime.now().toDatabaseString(),
//                            updatedAt = LocalDateTime.now().toDatabaseString()
//                        ).apply {
//
//                            val result = offersDataSource.addOffers(this)
//
//                            if (result > 0) {
//                                call.respond(
//                                    HttpStatusCode.OK, MyResponse(
//                                        success = true,
//                                        message = "Offer inserted successfully .",
//                                        data = this
//                                    )
//                                )
//                                return@post
//                            } else {
//                                call.respond(
//                                    HttpStatusCode.OK,
//                                    MyResponse(
//                                        success = false,
//                                        message = "Offer inserted failed .",
//                                        data = null
//                                    )
//                                )
//                                return@post
//                            }
//                        }
//                    } else {
//                        storageService.deleteOfferImages(fileName = fileName!!)
//                        call.respond(
//                            status = HttpStatusCode.OK,
//                            message = MyResponse(
//                                success = false,
//                                message = "some Error happened while uploading .",
//                                data = null
//                            )
//                        )
//                        return@post
//                    }
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = false,
                            message = "Offer inserted before .",
                            data = null
                        )
                    )
                    return@post
                }
            } catch (exc: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = exc.message ?: "Creation Failed .",
                        data = null
                    )
                )
                return@post
            }


        }
        //delete type category //api/v1/admin-client/offer/delete/{id}
        delete("$DELETE_OFFERS/{id}") {
            try {
                logger.debug { "get /$DELETE_OFFERS/{id}" }
                val id = call.parameters["id"]?.toIntOrNull()
                id?.let { offerId ->
                    offersDataSource.getOffersById(offerId)?.let { offer ->

                        val isDeleted = try {
                            storageService.deleteOfferImages(fileName = offer.image!!.substringAfterLast("/"))
                        } catch (e: Exception) {
                            call.respond(
                                HttpStatusCode.InternalServerError,
                                MyResponse(
                                    success = false,
                                    message = e.message ?: "Failed to delete icon",
                                    data = null
                                )
                            )
                            return@delete
                        }
                        if (isDeleted) {
                            val deleteResult = offersDataSource.deleteOffers(offerId)
                            if (deleteResult > 0) {
                                call.respond(
                                    HttpStatusCode.OK,
                                    MyResponse(
                                        success = true,
                                        message = "Offer deleted successfully .",
                                        data = null
                                    )
                                )
                            } else {
                                call.respond(
                                    HttpStatusCode.NotFound,
                                    MyResponse(
                                        success = false,
                                        message = " Offer deleted failed .",
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
                            message = " Offer deleted failed .",
                            data = null
                        )
                    )


                } ?: call.respond(
                    HttpStatusCode.BadRequest,
                    MyResponse(
                        success = false,
                        message = "Missing parameters .",
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
                return@delete
            }
        }
    }

}
