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


const val ALL_OFFERS = "${ADMIN_CLIENT}/offers"
const val ALL_OFFERS_PAGEABLE = "${ALL_OFFERS}-pageable"
const val SINGLE_OFFERS = "${ADMIN_CLIENT}/offer"
const val CREATE_OFFERS = "${SINGLE_OFFERS}/create"
const val UPDATE_OFFERS = "${SINGLE_OFFERS}/update"
const val DELETE_OFFERS = "${SINGLE_OFFERS}/delete"
const val DELETE_ALL_OFFERS = "${SINGLE_OFFERS}/delete-all"


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
                throw UnknownErrorException(e.message ?: "An unknown error occurred  ")
            }


        }


        //get offer //api/v1/admin-client/offer/{id}
        get("$SINGLE_OFFERS/{id}") {
            try {
                logger.debug { "get /$SINGLE_OFFERS/{id}" }
                call.parameters["id"]?.toIntOrNull()?.let { id ->
                    offersDataSource.getOffersByIdDto(id)?.let { offer ->
                        respondWithSuccessfullyResult(
                            result = offer,
                            message = "offer is found ."
                        )
                    } ?: throw NotFoundException("no Offer found .")
                } ?: throw MissingParameterException("Missing parameters .")

            } catch (exc: Exception) {
                throw ErrorException(exc.message ?: "Some Thing Goes Wrong .")
            }
        }
        //post type category //api/v1/admin-client/offer/create
        post(CREATE_OFFERS) {
            logger.debug { "POST /$CREATE_OFFERS" }

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
                throw ErrorException(e.stackTraceToString() ?: "Some Thing Goes Wrong .")

            }
        }
        //delete type offer //api/v1/admin-client/offer/delete/{id}
        delete("$DELETE_OFFERS/{id}") {
            try {
                logger.debug { "get /$DELETE_OFFERS/{id}" }
                call.parameters["id"]?.toIntOrNull()?.let { offerId ->
                    offersDataSource.getOffersById(offerId)?.let { offer ->
                        val oldImageName = offer.image?.substringAfterLast("/")
                        logger.debug { "try to delete oldImageName $oldImageName" }
                        oldImageName?.let {
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
                    }

                } ?: throw MissingParameterException("Missing parameters .")
            } catch (exc: Exception) {
                throw UnknownErrorException(exc.message ?: "An unknown error occurred")
            }
        }
        //put size offer //api/v1/admin-client/offer/update/{id}
        put("$UPDATE_OFFERS/{id}") {
            try {
                logger.debug { "get /$UPDATE_OFFERS/{id}" }
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

                    if (
                        isSameName &&
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
                    logger.debug { "try to save new icon in storage" }


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
                    logger.debug { "try to save ceramic product info in db" }

                    val updateResult = offersDataSource
                        .updateOffers(id, offerDto.toEntity(userId))
                    logger.debug { "ceramic product info save successfully in db" }
                    if (updateResult > 0) {
                        val updatedCategory = offersDataSource
                            .getOfferByTitleDto(newName)
                            ?: throw NotFoundException("ceramic product name ($newName) is not found ")

                        respondWithSuccessfullyResult(
                            result = updatedCategory,
                            message = "ceramic product updated successfully ."
                        )
                    } else {
                        throw UnknownErrorException("update failed .")

                    }

                } ?: throw MissingParameterException("Missing parameters .")

            } catch (exc: Exception) {
                throw UnknownErrorException(exc.message ?: "An unknown error occurred  ")

            }
        }
    }

}
