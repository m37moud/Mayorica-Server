package com.example.route.client_admin_side

import com.example.data.gallery.products.ProductDataSource
import com.example.mapper.toEntity
import com.example.models.MyResponsePageable
import com.example.models.dto.CeramicCreateDto
import com.example.models.options.getCeramicProductOptions

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


/**
 * we need
 * create
 * update
 * delete
 * MySQL80
 */

private const val ALL_PRODUCTS = "$ADMIN_CLIENT/products"
private const val ALL_PRODUCTS_PAGEABLE = "$ALL_PRODUCTS-pageable"
private const val SEARCH_PRODUCTS = "$ALL_PRODUCTS/search"
private const val SINGLE_PRODUCT = "$ADMIN_CLIENT/product"
private const val CREATE_SINGLE_PRODUCT = "$SINGLE_PRODUCT/create"
private const val UPDATE_SINGLE_PRODUCT = "$SINGLE_PRODUCT/update"
private const val DELETE_SINGLE_PRODUCT = "$SINGLE_PRODUCT/delete"
private const val PRODUCTS_TYPE_MENU = "$ALL_PRODUCTS/typeMenu"
private const val PRODUCTS_SIZE_MENU = "$ALL_PRODUCTS/sizeMenu"
private const val PRODUCTS_COLOR_MENU = "$ALL_PRODUCTS/colorMenu"


private val logger = KotlinLogging.logger {}

fun Route.productAdminRoute() {
    val productDataSource: ProductDataSource by inject()
    val storageService: StorageService by inject()
    val imageValidator: ImageValidator by inject()

    authenticate {
        //post products //api/v1/admin-client/products/create
        post(CREATE_SINGLE_PRODUCT) {
            logger.debug { "POST /$CREATE_SINGLE_PRODUCT" }

            try {
                val multiPart = receiveMultipart<CeramicCreateDto>(imageValidator)
                val userId = extractAdminId()
                val generateNewName = generateSafeFileName(multiPart.fileName)
                val url = "${multiPart.baseUrl}products/${generateNewName}"
                val imageUrl = multiPart.image?.let { img ->
                    storageService.saveProductImage(
                        fileName = generateNewName,
                        fileUrl = url,
                        fileBytes = img
                    )
                }
                val ceramicProductDto = multiPart.data.copy(productImageUrl = imageUrl!!)
                val createdCategory = productDataSource
                    .addCeramicProduct(ceramicProductDto.toEntity(userId))

                respondWithSuccessfullyResult(
                    result = createdCategory,
                    message = "ceramic product inserted successfully ."
                )
            } catch (exc: Exception) {
                throw ErrorException(exc.message ?: "Creation Failed .")
            }


        }
        get(ALL_PRODUCTS) {
            logger.debug { "GET ALL /$ALL_PRODUCTS" }
            try {
                val sizeCategoriesList = productDataSource.getAllProductDto()
                if (sizeCategoriesList.isEmpty()) throw NotFoundException("size categories is empty")
                respondWithSuccessfullyResult(
                    message = "get all ceramic product successfully",
                    result = sizeCategoriesList
                )

            } catch (exc: Exception) {
                throw UnknownErrorException(exc.message ?: "An unknown error occurred  ")
            }

        }
        // get the products --> get /api/v1/admin-client/products-pageable (token required)
        get(ALL_PRODUCTS_PAGEABLE) {
            logger.debug { "GET ALL /$ALL_PRODUCTS_PAGEABLE" }

            try {
                val params = call.request.queryParameters
                val ceramicOption = getCeramicProductOptions(params)
                val ceramicList =
                    productDataSource
                        .getAllProductPageable(
                            query = ceramicOption.query,
                            page = ceramicOption.page!!,
                            perPage = ceramicOption.perPage!!,
                            byTypeCategoryId = ceramicOption.byTypeCategoryId,
                            bySizeCategoryId = ceramicOption.bySizeCategoryId,
                            isHot = ceramicOption.isHot,
                            sortField = ceramicOption.sortFiled!!,
                            sortDirection = ceramicOption.sortDirection!!
                        )
                if (ceramicList.isEmpty()) throw NotFoundException("no product is found.")
                val numberOfProducts = productDataSource.getNumberOfProduct()
                respondWithSuccessfullyResult(
                    statusCode = HttpStatusCode.OK,
                    result = MyResponsePageable(
                        page = ceramicOption.page + 1,
                        perPage = numberOfProducts,
                        data = ceramicList
                    ),
                    message = "get all ceramic products successfully"
                )
            } catch (e: Exception) {
                throw UnknownErrorException(e.message ?: "An unknown error occurred  ")
            }


        }
        //delete product //api/v1/admin-client/product/{id}
        get("$SINGLE_PRODUCT/{id}") {
            try {
                logger.debug { "get /$SINGLE_PRODUCT/{id}" }
                call.parameters["id"]?.toIntOrNull()?.let {
                    productDataSource.getProductByIdDto(it)?.let { ceramicProduct ->

                        respondWithSuccessfullyResult(
                            statusCode = HttpStatusCode.OK,
                            result = ceramicProduct,
                            message = "ceramic product is found ."
                        )
                    } ?: throw NotFoundException("no ceramic product found .")


                } ?: throw MissingParameterException("Missing parameters .")


            } catch (exc: Exception) {
                throw UnknownErrorException(exc.message ?: "An unknown error occurred  ")

            }
        }
        //put size category //api/v1/admin-client/category/size/update/{id}
        put("$UPDATE_SINGLE_PRODUCT/{id}") {
            try {
                logger.debug { "get /$UPDATE_SINGLE_PRODUCT/{id}" }
                val multiPart = receiveMultipart<CeramicCreateDto>(imageValidator)
                val userId = extractAdminId()
                call.parameters["id"]?.toIntOrNull()?.let { id ->
                    val tempProduct = productDataSource.getProductById(id)
                        ?: throw NotFoundException("no ceramic product found .")
                    val newName = multiPart.data.productName
                    logger.debug { "check if ($newName) the new name if not repeat" }
//                    val checkProduct = productDataSource.getProductByName(newName)
                    val oldImageName = tempProduct.image.substringAfterLast("/")
                    val responseFileName = multiPart.fileName
                    logger.debug { "check oldImage ($oldImageName) and response (${responseFileName}) image new name if not repeat" }
                    val isSameName = tempProduct.productName == multiPart.data.productName
                    val isSameTypeMenu = tempProduct.typeCategoryId == multiPart.data.typeCategoryId
                    val isSameSizeMenu = tempProduct.sizeCategoryId == multiPart.data.sizeCategoryId
                    val isSameColorMenu = tempProduct.colorCategoryId == multiPart.data.colorCategoryId

                    if (
                        isSameName && isSameTypeMenu &&
                        isSameSizeMenu && isSameColorMenu &&
                        oldImageName == multiPart.fileName
                    ) {

                        throw AlreadyExistsException("that name ($newName) is already found ")
                    }
                    /**
                     * get old image url to delete
                     */
                    logger.debug { "oldImageName is  : $oldImageName" }
                    logger.debug { "try to delete old Image from storage first extract oldImageName " }

                    storageService.deleteProductImage(oldImageName)

                    logger.info { "old Image is deleted successfully from storage" }
                    logger.debug { "try to save new icon in storage" }


                    val generateNewName = generateSafeFileName(responseFileName)
                    val url = "${multiPart.baseUrl}products/${generateNewName}"

                    val imageUrl = multiPart.image?.let { img ->
                        storageService.saveProductImage(
                            fileName = generateNewName,
                            fileUrl = url,
                            fileBytes = img
                        )
                    }
                    val typeCategoryDto = multiPart.data
                        .copy(productImageUrl = imageUrl!!)
                    logger.debug { "try to save ceramic product info in db" }

                    productDataSource
                        .updateProduct(id, typeCategoryDto.toEntity(userId))
                    logger.debug { "ceramic product info save successfully in db" }

                    val updatedCategory = productDataSource
                        .getProductByNameDto(newName)
                        ?: throw NotFoundException("ceramic product name ($newName) is not found ")

                    respondWithSuccessfullyResult(
                        result = updatedCategory,
                        message = "ceramic product updated successfully ."
                    )

                } ?: throw MissingParameterException("Missing parameters .")

            } catch (exc: Exception) {
                throw UnknownErrorException(exc.message ?: "An unknown error occurred  ")

            }
        }
        //delete product //api/v1/admin-client/product/delete/{id}
        delete("$DELETE_SINGLE_PRODUCT/{id}") {
            try {
                logger.debug { "get /$DELETE_SINGLE_PRODUCT/{id}" }
                call.parameters["id"]?.toIntOrNull()?.let {
                    productDataSource.getProductByIdDto(it)?.let { ceramicProduct ->
                        val oldImageName = ceramicProduct.image.substringAfterLast("/")
                        logger.debug { "try to delete oldImageName $oldImageName" }

                        storageService.deleteProductImage(fileName = oldImageName)

                        productDataSource.deleteProduct(it)

                        respondWithSuccessfullyResult(
                            result = true,
                            message = "ceramic product deleted successfully ."
                        )

                    }

                } ?: throw MissingParameterException("Missing parameters .")

            } catch (exc: Exception) {
                throw UnknownErrorException(exc.message ?: "An unknown error occurred")

            }
        }

        //get product type menu //api/v1/admin-client/products/typeMenu
        get(PRODUCTS_TYPE_MENU) {
            logger.debug { "GET ALL /$PRODUCTS_TYPE_MENU" }
            try {
                val typeMenu = productDataSource.getAllTypeCategoryMenu()
                if (typeMenu.isEmpty()) throw NotFoundException("type menu is empty")
                logger.debug { "GET ALL /$PRODUCTS_TYPE_MENU sizeMenu = $typeMenu" }

                respondWithSuccessfullyResult(
                    message = "get all type menu categories successfully",
                    result = typeMenu
                )
            } catch (e: Exception) {
                throw UnknownErrorException(e.message ?: "An unknown error occurred  ")
            }

        }

        //get product size menu //api/v1/admin-client/products/sizeMenu
        get("$PRODUCTS_SIZE_MENU/{id}") {
            logger.debug { "GET ALL /$PRODUCTS_SIZE_MENU" }
            try {
               val typeId = call.parameters["id"]?.toIntOrNull()
                val sizeMenu = productDataSource.getAllSizeCategoryMenu(typeId)

//                if (sizeMenu.isEmpty()) throw NotFoundException("size menu is empty")
                logger.debug { "GET ALL /$PRODUCTS_SIZE_MENU sizeMenu = $sizeMenu" }

                respondWithSuccessfullyResult(
                    message = "get all size menu categories successfully",
                    result = sizeMenu
                )
            } catch (e: Exception) {
                throw UnknownErrorException(e.message ?: "An unknown error occurred  ")
            }

        }
        //get product color menu //api/v1/admin-client/products/sizeMenu
        get(PRODUCTS_COLOR_MENU) {
            logger.debug { "GET ALL /$PRODUCTS_COLOR_MENU" }
            try {
                val colorMenu = productDataSource.getAllColorCategoryMenu()

                if (colorMenu.isEmpty()) throw NotFoundException("color menu is empty")
                logger.debug { "GET ALL /$PRODUCTS_COLOR_MENU sizeMenu = $colorMenu" }

                respondWithSuccessfullyResult(
                    message = "get all color menu categories successfully",
                    result = colorMenu
                )
            } catch (e: Exception) {
                throw UnknownErrorException(e.message ?: "An unknown error occurred  ")
            }

        }


        /**
         * old
         */
        get(SEARCH_PRODUCTS) {

            call.request.queryParameters["product_name"]?.let { name ->
                logger.debug { "GET ALL /$SEARCH_PRODUCTS?product_name=$name" }

                val productList = productDataSource.searchProductByName(productName = name)
                if (productList.isNotEmpty()) {
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = MyResponse(success = true, message = "Fetch Product successfully", data = productList)
                    )

                } else {
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = MyResponse(success = false, message = "Product Not Found", data = null)
                    )
                }

            } ?: call.respond(
                status = HttpStatusCode.BadRequest,
                message = MyResponse(success = false, message = "Missing Some Fields", data = null)
            )

        }


    }

}


