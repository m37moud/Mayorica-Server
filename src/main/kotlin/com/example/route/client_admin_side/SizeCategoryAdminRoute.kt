package com.example.route.client_admin_side

import com.example.data.gallery.categories.size.SizeCategoryDataSource
import com.example.mapper.toEntity
import com.example.models.SizeCategory
import com.example.models.dto.SizeCategoryCreateDto
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

private const val CATEGORIES = "${ADMIN_CLIENT}/categories"
private const val CATEGORY = "$ADMIN_CLIENT/category"
private const val SIZE_CATEGORIES = "$CATEGORIES/size"
private const val SIZE_CATEGORY = "$CATEGORY/size"
private const val CREATE_SIZE_CATEGORY = "$SIZE_CATEGORY/create"
private const val UPDATE_SIZE_CATEGORY = "$SIZE_CATEGORY/update"
private const val DELETE_SIZE_CATEGORY = "$SIZE_CATEGORY/delete"

private val logger = KotlinLogging.logger {}

fun Route.sizeCategoryAdminRoute(){
    val sizeCategoryDataSource: SizeCategoryDataSource by inject()
    val storageService: StorageService by inject()
    val imageValidator: ImageValidator by inject()
    //post size category //api/v1/admin-client/category/size/create
    post(CREATE_SIZE_CATEGORY) {
        logger.debug { "POST /$CREATE_SIZE_CATEGORY" }
        val multiPart = receiveMultipart<SizeCategoryCreateDto>(imageValidator)
        val userId = extractAdminId()
        val generateNewName = generateSafeFileName(multiPart.fileName)
        val url = "${multiPart.baseUrl}categories/images/${generateNewName}"
        val imageUrl = multiPart.image?.let { img ->
            storageService.saveCategoryImages(
                fileName = generateNewName,
                fileUrl = url, fileBytes = img
            )
        }
        val sizeCategoryDto = multiPart.data.copy(sizeImageUrl = imageUrl!!)
        val createdCategory = sizeCategoryDataSource
            .addSizeCategory(sizeCategoryDto.toEntity(userId))
        respondWithSuccessfullyResult(
            statusCode = HttpStatusCode.OK,
            result = createdCategory,
            message = "Size Category inserted successfully ."
        )

        /**
         *
         */

        var typeCategoryId: Int? = null
        var size: String? = null
        var fileName: String? = null
        var fileBytes: ByteArray? = null


        try {


            val sizeCategory = typeCategoryDataSource.getTypeCategoryByName(size!!)
            if (sizeCategory == null) {
                imageUrl = try {
                    storageService.saveCategoryImages(
                        fileName = fileName!!,
                        fileUrl = url!!,
                        fileBytes = fileBytes!!
                    )
                } catch (e: Exception) {
                    storageService.deleteCategoryImages(fileName = fileName!!)
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
                SizeCategory(
                    typeCategoryId = typeCategoryId!!,
                    size = size!!,
                    sizeImage = imageUrl!!,
                    userAdminID = userId!!,
                    createdAt = LocalDateTime.now().toDatabaseString(),
                    updatedAt = LocalDateTime.now().toDatabaseString()
                ).apply {
                    val result = sizeCategoryDataSource.createSizeCategory(this)
                    if (result > 0) {
                        call.respond(
                            HttpStatusCode.OK, MyResponse(
                                success = true,
                                message = "Size Category inserted successfully .",
                                data = this
                            )
                        )
                        return@post
                    } else {
                        call.respond(
                            HttpStatusCode.OK, MyResponse(
                                success = false,
                                message = "Size Category inserted failed .",
                                data = null
                            )
                        )
                        return@post
                    }
                }


            } else {
                call.respond(
                    HttpStatusCode.OK, MyResponse(
                        success = false,
                        message = "Size Category inserted before .",
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

}