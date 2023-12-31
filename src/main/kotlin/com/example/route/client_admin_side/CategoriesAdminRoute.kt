package com.example.route.client_admin_side

import com.example.data.gallery.categories.CategoryDataSource
import com.example.data.gallery.products.ProductDataSource
import com.example.mapper.toModelCreate
import com.example.models.*
import com.example.models.request.categories.ColorCategoryRequest
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

const val CATEGORIES = "$ADMIN_CLIENT/categories"
const val TYPE_CATEGORIES = "$CATEGORIES/type"
const val SIZE_CATEGORIES = "$CATEGORIES/size"
const val COLOR_CATEGORIES = "$CATEGORIES/color"
const val CATEGORY = "$ADMIN_CLIENT/category"
const val TYPE_CATEGORY = "$CATEGORY/type"
const val CREATE_TYPE_CATEGORY = "$TYPE_CATEGORY/create"
const val UPDATE_TYPE_CATEGORY = "$TYPE_CATEGORY/update"
const val DELETE_TYPE_CATEGORY = "$TYPE_CATEGORY/delete"
const val SIZE_CATEGORY = "$CATEGORY/size"
const val CREATE_SIZE_CATEGORY = "$SIZE_CATEGORY/create"
const val UPDATE_SIZE_CATEGORY = "$SIZE_CATEGORY/update"
const val DELETE_SIZE_CATEGORY = "$SIZE_CATEGORY/delete"
const val COLOR_CATEGORY = "$CATEGORY/color"
const val CREATE_COLOR_CATEGORY = "$COLOR_CATEGORY/create"
const val UPDATE_COLOR_CATEGORY = "$COLOR_CATEGORY/update"
const val DELETE_COLOR_CATEGORY = "$COLOR_CATEGORY/delete"

private val logger = KotlinLogging.logger {}

fun Route.categoriesAdminRoute(
//    categoryDataSource: CategoryDataSource,
//    storageService: StorageService
) {
    val categoryDataSource: CategoryDataSource by inject()
    val storageService: StorageService by inject()

    authenticate {
        /**
         * create new category
         */
        //post type category //api/v1/admin-client/category/type/create
        post(CREATE_TYPE_CATEGORY) {
            logger.debug { "POST /$CREATE_TYPE_CATEGORY" }
            val multiPart = call.receiveMultipart()
            var typeCategoryName: String? = null
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
                                "typeCategoryName" -> {
                                    typeCategoryName = part.value
                                }


                            }

                        }

                        is PartData.FileItem -> {
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
                            url = "${baseUrl}categories/icons/${fileName}"

                        }

                        else -> {}

                    }
                    part.dispose()
                }


                val typeCategory = categoryDataSource.getTypeCategoryByName(typeCategoryName!!)
                if (typeCategory == null) {
                    imageUrl = try {
                        storageService.saveCategoryIcons(
                            fileName = fileName!!,
                            fileUrl = url!!,
                            fileBytes = fileBytes!!
                        )
                    } catch (e: Exception) {
                        storageService.deleteCategoryIcons(fileName = fileName!!)
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
                    if (!imageUrl.isNullOrEmpty()) {

                        TypeCategory(
                            typeName = typeCategoryName!!,
                            typeIcon = imageUrl,
                            userAdminID = userId!!,
                            createdAt = LocalDateTime.now().toDatabaseString(),
                            updatedAt = LocalDateTime.now().toDatabaseString()
                        ).apply {

                            val result = categoryDataSource.createTypeCategory(this)

                            if (result > 0) {
                                call.respond(
                                    HttpStatusCode.OK, MyResponse(
                                        success = true,
                                        message = "Type Category inserted successfully .",
                                        data = this
                                    )
                                )
                                return@post
                            } else {
                                call.respond(
                                    HttpStatusCode.OK,
                                    MyResponse(
                                        success = false,
                                        message = "Type Category inserted failed .",
                                        data = null
                                    )
                                )
                                return@post
                            }
                        }
                    } else {
                        storageService.deleteCategoryIcons(fileName = fileName!!)
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = MyResponse(
                                success = false,
                                message = "some Error happened while uploading .",
                                data = null
                            )
                        )
                        return@post
                    }
                } else {
                    call.respond(
                        HttpStatusCode.OK, MyResponse(
                            success = false,
                            message = "Type Category inserted before .",
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
        //post size category //api/v1/admin-client/category/size/create
        post(CREATE_SIZE_CATEGORY) {
            logger.debug { "POST /$CREATE_SIZE_CATEGORY" }

            val multiPart = call.receiveMultipart()
            var typeCategoryId: Int? = null
            var size: String? = null
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
                                "typeCategoryId" -> {
                                    typeCategoryId = part.value.toIntOrNull()
                                }

                                "sizeCategory" -> {
                                    size = part.value
                                }

                            }

                        }

                        is PartData.FileItem -> {
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
                            url = "${baseUrl}categories/images/${fileName}"

                        }

                        else -> {}

                    }
                    part.dispose()
                }


                val sizeCategory = categoryDataSource.getTypeCategoryByName(size!!)
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
                        val result = categoryDataSource.createSizeCategory(this)
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
        //post color category //api/v1/admin-client/category/color/create
        post(CREATE_COLOR_CATEGORY) {
            logger.debug { "POST /$CREATE_COLOR_CATEGORY" }

            val colorCategoryRequest = try {
                call.receive<ColorCategoryRequest>()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = "Missing Some Fields",
                        data = null
                    )
                )
                return@post
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
                return@post
            }

            try {
                val typeCategory = categoryDataSource.getColorCategoryByName(colorCategoryRequest.color)
                if (typeCategory == null) {

                    val result = categoryDataSource.createColorCategory(colorCategoryRequest.toModelCreate(userId!!))

                    if (result > 0) {
                        call.respond(
                            HttpStatusCode.OK, MyResponse(
                                success = true,
                                message = "Color Category inserted successfully .",
                                data = colorCategoryRequest.color
                            )
                        )
                        return@post
                    } else {
                        call.respond(
                            HttpStatusCode.OK, MyResponse(
                                success = false,
                                message = "Color Category inserted failed .",
                                data = null
                            )
                        )
                        return@post
                    }
                } else {
                    call.respond(
                        HttpStatusCode.OK, MyResponse(
                            success = false,
                            message = "Color Category inserted before .",
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
        /**
         * get all categories
         */
        //get all type category //api/v1/admin-client/categories/type
        get(TYPE_CATEGORIES) {
            try {
                // QueryParams: type categories?page=1&perPage=10
                call.request.queryParameters["page"]?.toIntOrNull()?.let {
                    val page = if (it > 0) it else 0
                    val perPage = call.request.queryParameters["perPage"]?.toIntOrNull() ?: 10

                    logger.debug { "GET ALL /$TYPE_CATEGORIES?page=$page&perPage=$perPage" }

                    val typeCategoriesList = categoryDataSource.getAllTypeCategoryPageable(page, perPage)
                    if (typeCategoriesList.isNotEmpty()) {
                        call.respond(
                            HttpStatusCode.OK, MyResponse(
                                success = true,
                                message = "get all type categories successfully",
                                data = TypeCategoryPage(page, perPage, typeCategoriesList)
                            )
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound, MyResponse(
                                success = false,
                                message = "type categories is empty",
                                data = null
                            )
                        )
                    }

                } ?: run {
                    logger.debug { "GET ALL /$TYPE_CATEGORIES" }

                    val typeCategoriesList = categoryDataSource.getAllTypeCategory()
                    if (typeCategoriesList.isNotEmpty()) {
                        call.respond(
                            HttpStatusCode.OK, MyResponse(
                                success = true,
                                message = "get all type categories successfully",
                                data = typeCategoriesList
                            )
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound, MyResponse(
                                success = false,
                                message = "type categories is empty",
                                data = null
                            )
                        )
                    }
                }
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
        //get all size category //api/v1/admin-client/categories/size
        get(SIZE_CATEGORIES) {
            try {
                // QueryParams: type categories?page=1&perPage=10
                call.request.queryParameters["page"]?.toIntOrNull()?.let {
                    val page = if (it > 0) it else 0
                    val perPage = call.request.queryParameters["perPage"]?.toIntOrNull() ?: 10

                    logger.debug { "GET ALL /$SIZE_CATEGORIES?page=$page&perPage=$perPage" }

                    val sizeCategoriesList = categoryDataSource.getAllSizeCategoryPageable(page, perPage)
                    if (sizeCategoriesList.isNotEmpty()) {
                        call.respond(
                            HttpStatusCode.OK, MyResponse(
                                success = true,
                                message = "get all size categories successfully",
                                data = SizeCategoryPage(page, perPage, sizeCategoriesList)
                            )
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound, MyResponse(
                                success = false,
                                message = "no size categories is found",
                                data = null
                            )
                        )
                    }

                } ?: run {
                    logger.debug { "GET ALL /$SIZE_CATEGORIES" }

                    val typeCategoriesList = categoryDataSource.getAllSizeCategory()
                    if (typeCategoriesList.isNotEmpty()) {
                        call.respond(
                            HttpStatusCode.OK, MyResponse(
                                success = true,
                                message = "get all size categories successfully",
                                data = typeCategoriesList
                            )
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound, MyResponse(
                                success = false,
                                message = "size categories is not found",
                                data = null
                            )
                        )
                    }
                }
            } catch (exc: Exception) {
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = false,
                        message = exc.message ?: "Failed ",
                        data = null
                    )
                )
                return@get
            }
        }
        //get all type category //api/v1/admin-client/categories/color
        get(COLOR_CATEGORIES) {
            try {
                // QueryParams: type categories?page=1&perPage=10
                call.request.queryParameters["page"]?.toIntOrNull()?.let {
                    val page = if (it > 0) it else 0
                    val perPage = call.request.queryParameters["perPage"]?.toIntOrNull() ?: 10

                    logger.debug { "GET ALL /$TYPE_CATEGORIES?page=$page&perPage=$perPage" }

                    val colorCategoriesList = categoryDataSource.getAllColorCategoryPageable(page, perPage)
                    if (colorCategoriesList.isNotEmpty()) {
                        call.respond(
                            HttpStatusCode.OK, MyResponse(
                                success = true,
                                message = "get all size categories successfully",
                                data = ColorCategoryPage(page, perPage, colorCategoriesList)
                            )
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound, MyResponse(
                                success = false,
                                message = "no color categories is found",
                                data = null
                            )
                        )
                    }

                } ?: run {
                    logger.debug { "GET ALL /$TYPE_CATEGORIES" }

                    val typeCategoriesList = categoryDataSource.getAllTypeCategory()
                    if (typeCategoriesList.isNotEmpty()) {
                        call.respond(
                            HttpStatusCode.OK, MyResponse(
                                success = true,
                                message = "get all color categories successfully",
                                data = typeCategoriesList
                            )
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound, MyResponse(
                                success = false,
                                message = "no color categories is found",
                                data = null
                            )
                        )
                    }
                }
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
        /**
         * get category by id
         */
        //get type category //api/v1/admin-client/category/type/{id}
        get("$TYPE_CATEGORY/{id}") {
            try {
                logger.debug { "get /$TYPE_CATEGORY/{id}" }
                val id = call.parameters["id"]?.toIntOrNull()
                id?.let {
                    categoryDataSource.getTypeCategoryById(it)?.let { typeCategory ->
                        call.respond(
                            HttpStatusCode.OK,
                            MyResponse(
                                success = true,
                                message = "type category is found .",
                                data = typeCategory
                            )
                        )
                    } ?: call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = false,
                            message = "no type category found .",
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
        //get size category //api/v1/admin-client/category/size/{id}
        get("$SIZE_CATEGORY/{id}") {
            try {
                logger.debug { "get /$SIZE_CATEGORY/{id}" }
                val id = call.parameters["id"]?.toIntOrNull()
                id?.let {
                    categoryDataSource.getSizeCategoryById(it)?.let { sizeCategory ->
                        call.respond(
                            HttpStatusCode.OK,
                            MyResponse(
                                success = true,
                                message = "get size category successfully .",
                                data = sizeCategory
                            )
                        )
                    } ?: call.respond(
                        HttpStatusCode.NotFound,
                        MyResponse(
                            success = false,
                            message = "no size category found .",
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
        //get color category //api/v1/admin-client/category/color/{id}
        get("$COLOR_CATEGORY/{id}") {
            try {
                logger.debug { "get /$TYPE_CATEGORY/{id}" }
                val id = call.parameters["id"]?.toIntOrNull()
                id?.let {
                    categoryDataSource.getColorCategoryById(it)?.let { colorCategory ->
                        call.respond(
                            HttpStatusCode.OK,
                            MyResponse(
                                success = true,
                                message = "get color category successfully .",
                                data = colorCategory
                            )
                        )
                    } ?: call.respond(
                        HttpStatusCode.NotFound,
                        MyResponse(
                            success = false,
                            message = "no color category found .",
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

        /**
         * delete category by id
         */
        //delete type category //api/v1/admin-client/category/type/delete/{id}
        delete("$DELETE_TYPE_CATEGORY/{id}") {
            try {
                logger.debug { "get /$DELETE_TYPE_CATEGORY/{id}" }
                val id = call.parameters["id"]?.toIntOrNull()
                id?.let {
                    categoryDataSource.getTypeCategoryById(it)?.let { typeCategory ->

                        val isDeletedIcon = try {
                            storageService.deleteCategoryIcons(fileName = typeCategory.typeIcon.substringAfterLast("/"))
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
                        if (isDeletedIcon) {
                            val deleteResult = categoryDataSource.deleteTypeCategory(it)
                            if (deleteResult > 0) {
                                call.respond(
                                    HttpStatusCode.OK,
                                    MyResponse(
                                        success = true,
                                        message = "type category deleted successfully .",
                                        data = null
                                    )
                                )
                            } else {
                                call.respond(
                                    HttpStatusCode.NotFound,
                                    MyResponse(
                                        success = false,
                                        message = " type category deleted failed .",
                                        data = null
                                    )
                                )
                            }

                        } else {
                            call.respond(
                                HttpStatusCode.Conflict,
                                MyResponse(
                                    success = false,
                                    message = "Failed to delete icon",
                                    data = null
                                )
                            )

                        }


                    } ?: call.respond(
                        HttpStatusCode.NotFound,
                        MyResponse(
                            success = false,
                            message = " type category deleted failed .",
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
        //delete size category //api/v1/admin-client/category/size/delete/{id}
        delete("$DELETE_SIZE_CATEGORY/{id}") {
            try {
                logger.debug { "delete /$DELETE_SIZE_CATEGORY/{id}" }
                val id = call.parameters["id"]?.toIntOrNull()
                id?.let {
                    categoryDataSource.getSizeCategoryById(it)?.let { sizeCategory ->
                        val isDeleted = try {
                            storageService.deleteCategoryImages(
                                fileName = sizeCategory.sizeImage.substringAfterLast("/"))
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
                            val deleteResult = categoryDataSource.deleteSizeCategory(it)
                            if (deleteResult > 0) {
                                call.respond(
                                    HttpStatusCode.OK,
                                    MyResponse(
                                        success = true,
                                        message = "size category deleted successfully .",
                                        data = null
                                    )
                                )
                            } else {
                                call.respond(
                                    HttpStatusCode.NotFound,
                                    MyResponse(
                                        success = false,
                                        message = "size category deleted failed .",
                                        data = null
                                    )
                                )
                            }
                        } else {
                            call.respond(
                                HttpStatusCode.Conflict,
                                MyResponse(
                                    success = false,
                                    message = "Failed to delete icon",
                                    data = null
                                )
                            )


                        }
                    }


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
        //delete color category //api/v1/admin-client/category/color/delete/{id}
        delete("$DELETE_COLOR_CATEGORY/{id}") {
            try {
                logger.debug { "get /$DELETE_COLOR_CATEGORY/{id}" }
                val id = call.parameters["id"]?.toIntOrNull()
                id?.let {
                    val deleteResult = categoryDataSource.deleteColorCategory(it)
                    if (deleteResult > 0) {
                        call.respond(
                            HttpStatusCode.OK,
                            MyResponse(
                                success = true,
                                message = "color category deleted successfully .",
                                data = null
                            )
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            MyResponse(
                                success = false,
                                message = "color category deleted failed .",
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


        /**
         * update
         */
        //put type category //api/v1/admin-client/category/type/update/{id}
        put("$UPDATE_TYPE_CATEGORY/{id}") {
            try {
                logger.debug { "get /$TYPE_CATEGORY/{id}" }
                val id = call.parameters["id"]?.toIntOrNull()
                id?.let {
                    val typeCategory = try {
                        call.receive<TypeCategory>()
                    } catch (exc: Exception) {
                        call.respond(
                            HttpStatusCode.Conflict,
                            MyResponse(
                                success = false,
                                message = exc.message ?: "update Failed .",
                                data = null
                            )
                        )
                        return@put
                    }
                    categoryDataSource.getTypeCategoryById(id)?.let { temp ->
                        val newTypeCategory = typeCategory.copy(
                            createdAt = temp.createdAt,
                            updatedAt = LocalDateTime.now().toDatabaseString()
                        )
                        val updateResult = categoryDataSource.updateTypeCategory(newTypeCategory)
                        if (updateResult > 0) {
                            call.respond(
                                HttpStatusCode.OK,
                                MyResponse(
                                    success = true,
                                    message = "type category updated successfully .",
                                    data = typeCategory
                                )
                            )
                        } else {
                            call.respond(
                                HttpStatusCode.OK,
                                MyResponse(
                                    success = false,
                                    message = " type category updated failed .",
                                    data = null
                                )
                            )
                        }
                    } ?: call.respond(
                        HttpStatusCode.NotFound,
                        MyResponse(
                            success = false,
                            message = "no type category is found .",
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
                return@put
            }
        }
        //put size category //api/v1/admin-client/category/size/update/{id}
        put("$UPDATE_SIZE_CATEGORY/{id}") {
            try {
                logger.debug { "get /$UPDATE_SIZE_CATEGORY/{id}" }
                val id = call.parameters["id"]?.toIntOrNull()
                id?.let {
                    val sizeCategory = try {
                        call.receive<SizeCategory>()
                    } catch (exc: Exception) {
                        call.respond(
                            HttpStatusCode.Conflict,
                            MyResponse(
                                success = false,
                                message = exc.message ?: "update Failed .",
                                data = null
                            )
                        )
                        return@put
                    }
                    categoryDataSource.getSizeCategoryById(it)?.let { temp ->
                        val newSizeCategory = sizeCategory.copy(
                            createdAt = temp.createdAt,
                            updatedAt = LocalDateTime.now().toDatabaseString()
                        )
                        val updateResult = categoryDataSource.updateSizeCategory(newSizeCategory)
                        if (updateResult > 0) {
                            call.respond(
                                HttpStatusCode.OK,
                                MyResponse(
                                    success = true,
                                    message = "size category updated successfully .",
                                    data = null
                                )
                            )
                        } else {
                            call.respond(
                                HttpStatusCode.OK,
                                MyResponse(
                                    success = false,
                                    message = " size category updated failed .",
                                    data = null
                                )
                            )
                        }
                    } ?: call.respond(
                        HttpStatusCode.NotFound,
                        MyResponse(
                            success = false,
                            message = "no size category found .",
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
                return@put
            }
        }
        //put color category //api/v1/admin-client/category/color/update/{id}
        put("$UPDATE_COLOR_CATEGORY/{id}") {
            try {
                logger.debug { "get /$UPDATE_COLOR_CATEGORY/{id}" }
                val id = call.parameters["id"]?.toIntOrNull()
                id?.let {
                    val colorCategory = try {
                        call.receive<ColorCategory>()
                    } catch (exc: Exception) {
                        call.respond(
                            HttpStatusCode.Conflict,
                            MyResponse(
                                success = false,
                                message = exc.message ?: "update Failed .",
                                data = null
                            )
                        )
                        return@put
                    }
                    categoryDataSource.getColorCategoryById(it)?.let { temp ->
                        val newColorCategory = colorCategory.copy(
                            createdAt = temp.createdAt,
                            updatedAt = LocalDateTime.now().toDatabaseString()
                        )
                        val updateResult = categoryDataSource.updateColorCategory(newColorCategory)
                        if (updateResult > 0) {
                            call.respond(
                                HttpStatusCode.OK,
                                MyResponse(
                                    success = true,
                                    message = "color category updated successfully .",
                                    data = null
                                )
                            )
                        } else {
                            call.respond(
                                HttpStatusCode.OK,
                                MyResponse(
                                    success = false,
                                    message = " color category updated failed .",
                                    data = null
                                )
                            )
                        }
                    } ?: call.respond(
                        HttpStatusCode.NotFound,
                        MyResponse(
                            success = false,
                            message = "no color category found .",
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
                return@put
            }
        }


    }
}