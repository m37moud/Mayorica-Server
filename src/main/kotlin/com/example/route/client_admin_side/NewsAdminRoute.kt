package com.example.route.client_admin_side

import com.example.data.news.NewsDataSource
import com.example.mapper.toEntity
import com.example.models.MyResponsePageable
import com.example.models.dto.NewsCreateDto
import com.example.models.options.getNewsOptions
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

private const val ALL_NEWS = "${ADMIN_CLIENT}/news"
const val ALL_NEWS_PAGEABLE = "${ALL_NEWS}-pageable"

private const val CREATE_NEWS = "$ALL_NEWS/create"
private const val UPDATE_NEWS = "$ALL_NEWS/update"
private const val DELETE_NEWS = "$ALL_NEWS/delete"

private val logger = KotlinLogging.logger { }

fun Route.newsAdminRoute(
//    newsDataSource: NewsDataSource,
//    storageService: StorageService

) {
    val newsDataSource: NewsDataSource by inject()
    val storageService: StorageService by inject()
    val imageValidator: ImageValidator by inject()


    authenticate {
        //get all News -> api/v1/admin-client/news
        get(ALL_NEWS) {
            logger.debug { "get all NEWS  $ALL_NEWS" }
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
        // get the News as pageable --> get /api/v1/admin-client/news-pageable (token required)
        get(ALL_NEWS_PAGEABLE) {
            logger.debug { "GET ALL /$ALL_NEWS_PAGEABLE" }

            try {
                val params = call.request.queryParameters
                val newsOption = getNewsOptions(params)
                val newsList =
                    newsDataSource
                        .getAllNewsPageable(
                            query = newsOption.query,
                            page = newsOption.page!!,
                            perPage = newsOption.perPage!!,
                            sortField = newsOption.sortFiled!!,
                            sortDirection = newsOption.sortDirection!!
                        )
                if (newsList.isEmpty()) throw NotFoundException("no news is found.")
                val numberOfNews = newsDataSource.getNumberOfNews()

                respondWithSuccessfullyResult(
                    result = MyResponsePageable(
                        page = newsOption.page + 1,
                        perPage = numberOfNews,
                        data = newsList
                    ),
                    message = "get all news successfully"
                )
            } catch (e: Exception) {
                logger.error { "${e.stackTrace ?: "An unknown error occurred"}" }
                throw UnknownErrorException(e.message ?: "An unknown error occurred  ")
            }


        }
        //get -> api/v1/admin-client/news/{id}
        get("$ALL_NEWS/{id}") {
            logger.debug { "get News $ALL_NEWS/{id}" }
            try {
                call.parameters["id"]?.toIntOrNull()?.let { id ->

                    newsDataSource.getNewsByIdDto(newsId = id)?.let { news ->
                        respondWithSuccessfullyResult(
                            result = news,
                            message = "get News Successfully ."
                        )
                    } ?: throw NotFoundException("no News found .")
                } ?: throw MissingParameterException("Missing parameters .")

            } catch (e: Exception) {
                logger.error { "get News error ${e.stackTrace ?: "An unknown error occurred  "}" }
                throw ErrorException(e.message ?: "An unknown error occurred  ")

            }

        }
        // post the NEWS --> POST /api/v1/admin-client/news/create (token required)
        post(CREATE_NEWS) {
            logger.debug { "POST /$CREATE_NEWS" }

            try {
                val multiPart = receiveMultipart<NewsCreateDto>(imageValidator)
                val userId = extractAdminId()
                val generateNewName = multiPart.fileName?.let { fileName ->

                    generateSafeFileName(fileName)
                }
                val url = "${multiPart.baseUrl}news/${generateNewName}"

                val imageUrl: String? = multiPart.image?.let { img ->
                    if (img.isNotEmpty() &&
                        !generateNewName.isNullOrEmpty()
                    )
                        storageService.saveNewsImage(
                            fileName = generateNewName,
                            fileUrl = url,
                            fileBytes = img
                        )
                    else null
                }
                logger.debug { "imageUrl =$imageUrl" }

                val newsDto = multiPart.data.copy(newsImageUrl = imageUrl)
                val createdNews = newsDataSource
                    .addNews(newsDto.toEntity(userId))
                respondWithSuccessfullyResult(
                    result = createdNews,
                    message = "News inserted successfully ."
                )
            } catch (e: Exception) {
                logger.error { "${e.stackTrace ?: "An unknown error occurred"}" }
                throw ErrorException(e.message ?: "Some Thing Goes Wrong .")

            }
        }

        // delete the NEWS --> delete /api/v1/admin-client/news/delete/{id} (token required)
        delete("$DELETE_NEWS/{id}") {
            logger.debug { "delete /$DELETE_NEWS" }
            try {
                call.parameters["id"]?.toIntOrNull()?.let { id ->

                    newsDataSource.getNewsById(newsId = id)?.let { news ->
                        val oldImageName = news.image?.substringAfterLast("/")
                        logger.debug { "try to delete oldImageName $oldImageName" }
                        oldImageName?.let {
                            if (it.isNotEmpty())
                                storageService.deleteNewsImages(fileName = it)
                        }

                        val deleteResult = newsDataSource.deleteNews(newsId = id)
                        if (deleteResult > 0) {
                            respondWithSuccessfullyResult(
                                result = true,
                                message = "News deleted successfully ."
                            )
                        } else {
                            throw UnknownErrorException("News deleted failed .")
                        }


                    } ?: throw NotFoundException("no News found .")
                } ?: throw MissingParameterException("Missing parameters .")
            } catch (e: Exception) {
                logger.error { "${e.stackTrace ?: "An unknown error occurred"}" }
                throw UnknownErrorException(e.message ?: "An unknown error occurred")

            }


        }

        //put size NEWS //api/v1/admin-client/news/update/{id}
        put("$UPDATE_NEWS/{id}") {
            try {
                logger.debug { "get /$UPDATE_NEWS/{id}" }
                val multiPart = receiveMultipart<NewsCreateDto>(imageValidator)
                val userId = extractAdminId()
                call.parameters["id"]?.toIntOrNull()?.let { id ->
                    val tempProduct = newsDataSource.getNewsById(id)
                        ?: throw NotFoundException("no News found .")
                    val newName = multiPart.data.newsTitle
                    logger.debug { "check if ($newName) the new name if not repeat" }
                    val oldImageName = tempProduct.image?.substringAfterLast("/")
                    val responseFileName = multiPart.fileName
                    logger.debug { "check oldImage ($oldImageName) and response (${responseFileName}) image new name if not repeat" }
                    val isSameName = tempProduct.title == multiPart.data.newsTitle
                    val isSameDescription = tempProduct.newsDescription == multiPart.data.newsDescription

                    if (
                        isSameName && isSameDescription &&
                        oldImageName == multiPart.fileName
                    ) {

                        throw AlreadyExistsException("that name ($newName) or image is already found ")
                    }
                    /**
                     * get old image url to delete
                     */
                    logger.debug { "oldImageName is  : $oldImageName" }
                    logger.debug { "try to delete old Image from storage first extract oldImageName " }
                    oldImageName?.let {
                        if (it.isNotEmpty())
                            storageService.deleteNewsImages(it)
                    }

                    logger.info { "old Image is deleted successfully from storage" }
                    logger.debug { "try to save new image in storage" }


                    val generateNewName = responseFileName?.let { generateSafeFileName(it) }
                    val url = "${multiPart.baseUrl}news/${generateNewName}"

                    val imageUrl = multiPart.image?.let { img ->
                        if (img.isNotEmpty() &&
                            !generateNewName.isNullOrEmpty()
                        )
                            storageService.saveNewsImage(
                                fileName = generateNewName,
                                fileUrl = url,
                                fileBytes = img
                            )
                        else null
                    }
                    logger.debug { "imageUrl =$imageUrl" }
                    val newsDto = multiPart.data.copy(newsImageUrl = imageUrl)
                    logger.debug { "try to save News info in db" }

                    val updateResult = newsDataSource
                        .updateNews(id, newsDto.toEntity(userId))
                    logger.debug { "News info save successfully in db" }
                    if (updateResult > 0) {
                        val updatedCategory = newsDataSource
                            .getNewsByTitleDto(newName)
                            ?: throw NotFoundException("Failed to get ($newName) after update ")

                        respondWithSuccessfullyResult(
                            result = updatedCategory,
                            message = "News Item updated successfully ."
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
    }

}