package com.example.route.client_admin_side

import com.example.data.offers.OffersDataSource
import com.example.mapper.toEntity
import com.example.models.MyResponsePageable
import com.example.models.dto.ProductOfferCreateDto
import com.example.models.options.getOfferOptions
import com.example.service.storage.StorageService
import com.example.utils.*
import com.example.utils.Constants.ADMIN_CLIENT
import com.example.utils.NotFoundException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import org.koin.ktor.ext.inject


private const val ALL_OFFERS = "${ADMIN_CLIENT}/offers"
private const val ALL_OFFERS_PAGEABLE = "${ALL_OFFERS}-pageable"
private const val SINGLE_OFFER = "${ADMIN_CLIENT}/offer"
private const val CREATE_OFFER = "${SINGLE_OFFER}/create"
private const val UPDATE_OFFER = "${SINGLE_OFFER}/update"
private const val DELETE_OFFER = "${SINGLE_OFFER}/delete"
private const val ADD_HOT_OFFER = "${SINGLE_OFFER}/hot-add"
private const val REMOVE_HOT_OFFER = "${SINGLE_OFFER}/hot-delete"
private const val DELETE_ALL_OFFERS = "${SINGLE_OFFER}/delete-all"


private val logger = KotlinLogging.logger { }


fun Route.offersAdminRoute(
) {
    val offersDataSource: OffersDataSource by inject()
    val storageService: StorageService by inject()
    val imageValidator: ImageValidator by inject()


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

        // get the offers as pageable --> get /api/v1/admin-client/offers-pageable (token required)
        get(ALL_OFFERS_PAGEABLE) {
            logger.debug { "GET ALL /$ALL_OFFERS_PAGEABLE" }

            try {
                val params = call.request.queryParameters
                val offerOption = getOfferOptions(params)
                val offerList =
                    offersDataSource
                        .getAllOffersPageable(
                            query = offerOption.query,
                            page = offerOption.page!!,
                            perPage = offerOption.perPage!!,
                            isHot = offerOption.isHot,
                            sortField = offerOption.sortFiled!!,
                            sortDirection = offerOption.sortDirection!!
                        )
                if (offerList.isEmpty()) throw NotFoundException("no offer is found.")
                val numberOfOffers = offersDataSource.getNumberOfOffers()

                respondWithSuccessfullyResult(
                    result = MyResponsePageable(
                        page = offerOption.page + 1,
                        perPage = numberOfOffers,
                        data = offerList
                    ),
                    message = "get all offers successfully"
                )
            } catch (e: Exception) {
                logger.error { "SINGLE_OFFER error ${e.stackTrace ?: "An unknown error occurred"}" }
                throw UnknownErrorException(e.message ?: "An unknown error occurred  ")
            }


        }


        //get offer //api/v1/admin-client/offer/{id}
        get("$SINGLE_OFFER/{id}") {
            try {
                logger.debug { "get /$SINGLE_OFFER/{id}" }
                call.parameters["id"]?.toIntOrNull()?.let { id ->
                    offersDataSource.getOffersByIdDto(id)?.let { offer ->
                        respondWithSuccessfullyResult(
                            result = offer,
                            message = "get Offer Successfully ."
                        )
                    } ?: throw NotFoundException("no Offer found .")
                } ?: throw MissingParameterException("Missing parameters .")

            } catch (exc: Exception) {
                logger.error { "get Offers error ${exc.stackTrace ?: "An unknown error occurred  "}" }
                throw ErrorException(exc.message ?: "Some Thing Goes Wrong .")
            }
        }
        //post type category //api/v1/admin-client/offer/create
        post(CREATE_OFFER) {
            logger.debug { "POST /$CREATE_OFFER" }

            try {
                val multiPart = receiveMultipart<ProductOfferCreateDto>(imageValidator)
                val userId = extractAdminId()
                val generateNewName = multiPart.fileName?.let { fileName ->

                    generateSafeFileName(fileName)
                }
                val url = "${multiPart.baseUrl}offers/${generateNewName}"

                val imageUrl: String? = multiPart.image?.let { img ->
                    if (img.isNotEmpty() &&
                        !generateNewName.isNullOrEmpty()
                    )
                        storageService.saveOfferImage(
                            fileName = generateNewName,
                            fileUrl = url,
                            fileBytes = img
                        )
                    else null
                }
                logger.debug { "imageUrl =$imageUrl" }

                val offerDto = multiPart.data.copy(offerImageUrl = imageUrl)
                val createdOffer = offersDataSource
                    .addOffers(offerDto.toEntity(userId))
                respondWithSuccessfullyResult(
                    result = createdOffer,
                    message = "Offer inserted successfully ."
                )
            } catch (e: Exception) {
                logger.error { "${e.stackTrace ?: "An unknown error occurred"}" }

                throw ErrorException(e.message ?: "Some Thing Goes Wrong .")

            }
        }
        //delete type offer //api/v1/admin-client/offer/delete/{id}
        delete("$DELETE_OFFER/{id}") {
            try {
                logger.debug { "get /$DELETE_OFFER/{id}" }
                call.parameters["id"]?.toIntOrNull()?.let { offerId ->
                    offersDataSource.getOffersById(offerId)?.let { offer ->
                        val oldImageName = offer.image?.substringAfterLast("/")
                        logger.debug { "try to delete oldImageName $oldImageName" }
                        oldImageName?.let {
                            if (it.isNotEmpty())
                                storageService.deleteOfferImages(fileName = it)
                        }
                        val deleteResult = offersDataSource.deleteOffers(offerId)
                        if (deleteResult > 0) {
                            respondWithSuccessfullyResult(
                                result = true,
                                message = "Offer deleted successfully ."
                            )
                        } else {
                            throw UnknownErrorException("Offer deleted failed .")
                        }
                    } ?: throw NotFoundException("no Offer found .")

                } ?: throw MissingParameterException("Missing parameters .")
            } catch (exc: Exception) {
                logger.error { "${exc.stackTrace ?: "An unknown error occurred"}" }
                throw UnknownErrorException(exc.message ?: "An unknown error occurred")
            }
        }
        //put size offer //api/v1/admin-client/offer/update/{id}
        put("$UPDATE_OFFER/{id}") {
            try {
                logger.debug { "get /$UPDATE_OFFER/{id}" }
                val multiPart = receiveMultipart<ProductOfferCreateDto>(imageValidator)
                val userId = extractAdminId()
                call.parameters["id"]?.toIntOrNull()?.let { id ->
                    val tempProduct = offersDataSource.getOffersById(id)
                        ?: throw NotFoundException("no Offer found .")
                    val newName = multiPart.data.offerTitle
                    logger.debug { "check if ($newName) the new name if not repeat" }
//                    val checkProduct = productDataSource.getProductByName(newName)
                    val oldImageName = tempProduct.image?.substringAfterLast("/")
                    val responseFileName = multiPart.fileName
                    logger.debug { "check oldImage ($oldImageName) and response (${responseFileName}) image new name if not repeat" }
                    val isSameName = tempProduct.title == multiPart.data.offerTitle
                    val isSameDescription = tempProduct.offerDescription == multiPart.data.offerDescription

                    if (
                        isSameName && isSameDescription &&
                        oldImageName == multiPart.fileName
                    ) {

                        throw AlreadyExistsException("that name ($newName) is already found ")
                    }
                    /**
                     * get old image url to delete
                     */
                    logger.debug { "oldImageName is  : $oldImageName" }
                    logger.debug { "try to delete old Image from storage first extract oldImageName " }
                    oldImageName?.let {
                        if (it.isNotEmpty())
                            storageService.deleteOfferImages(it)
                    }

                    logger.info { "old Image is deleted successfully from storage" }
                    logger.debug { "try to save new image in storage" }


                    val generateNewName = responseFileName?.let { generateSafeFileName(it) }
                    val url = "${multiPart.baseUrl}offers/${generateNewName}"

                    val imageUrl = multiPart.image?.let { img ->
                        if (img.isNotEmpty() &&
                            !generateNewName.isNullOrEmpty()
                        )
                            storageService.saveOfferImage(
                                fileName = generateNewName,
                                fileUrl = url,
                                fileBytes = img
                            )
                        else null
                    }
                    logger.debug { "imageUrl =$imageUrl" }
                    val offerDto = multiPart.data.copy(offerImageUrl = imageUrl)
                    logger.debug { "try to save Offer info in db" }

                    val updateResult = offersDataSource
                        .updateOffers(id, offerDto.toEntity(userId))
                    logger.debug { "Offer info save successfully in db" }
                    if (updateResult > 0) {
                        val updatedCategory = offersDataSource
                            .getOfferByTitleDto(newName)
                            ?: throw NotFoundException("Offer name ($newName) is not found ")

                        respondWithSuccessfullyResult(
                            result = updatedCategory,
                            message = "Offer Item updated successfully ."
                        )
                    } else {
                        throw UnknownErrorException("update failed .")

                    }

                } ?: throw MissingParameterException("Missing parameters .")

            } catch (exc: Exception) {
                logger.error { "${exc.stackTrace ?: "An unknown error occurred"}" }

                throw UnknownErrorException(exc.message ?: "An unknown error occurred  ")

            }
        }
        //put size offer //api/v1/admin-client/offer/hot-add/{id}
        put("$ADD_HOT_OFFER/{id}") {
            logger.debug { "get /$ADD_HOT_OFFER/{id}" }
            try {
                call.parameters["id"]?.toIntOrNull()?.let { id ->
                    if (offersDataSource.addToHotOffer(id) < 0)
                        throw ErrorException("Hot Offer Insert Failed")

                    respondWithSuccessfullyResult(
                        result = true,
                        message = "Hot Offer Insert successfully"
                    )

                } ?: throw MissingParameterException("Missing parameters .")
            } catch (e: Exception) {
                logger.error { "${e.stackTrace ?: "An unknown error occurred"}" }
                throw ErrorException(e.message ?: "An unknown error occurred")

            }


        }
        //put size offer //api/v1/admin-client/offer/hot-delete/{id}
        put("$REMOVE_HOT_OFFER/{id}") {
            logger.debug { "get /$REMOVE_HOT_OFFER/{id}" }
            try {
                call.parameters["id"]?.toIntOrNull()?.let { id ->
                    if (offersDataSource.removeFromHotOffer(id) < 0)
                        throw ErrorException("Hot Offer Insert Failed")

                    respondWithSuccessfullyResult(
                        result = true,
                        message = "Hot Offer Insert successfully"
                    )

                } ?: throw MissingParameterException("Missing parameters .")
            } catch (e: Exception) {
                logger.error { "${e.stackTrace ?: "An unknown error occurred"}" }
                throw ErrorException(e.message ?: "An unknown error occurred")

            }


        }


    }

}
