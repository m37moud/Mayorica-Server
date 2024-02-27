package com.example.route.client_admin_side

import com.example.data.administrations.apps.admin.AppsAdminDataSource
import com.example.data.administrations.apps.user.AppsUserDataSource
import com.example.mapper.toEntity
import com.example.models.AppsModel
import com.example.models.MyResponsePageable
import com.example.models.dto.AppCreateDto
import com.example.models.options.getAppsOptions
import com.example.utils.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import org.koin.ktor.ext.inject


private val logger = KotlinLogging.logger {}

/**
 * USER APP
 */
private const val ADMIN_APP = "${Constants.ADMIN_CLIENT}/admin-app"
private const val ALL_ADMIN_APP_PAGEABLE = "${ADMIN_APP}-pageable"
private const val CREATE_ADMIN_APP = "${ADMIN_APP}/create"
private const val UPDATE_ADMIN_APP = "${ADMIN_APP}/update"
private const val DELETE_ADMIN_APP = "${ADMIN_APP}/delete"

/**
 * USER APP
 */
private const val USER_APP = "${Constants.ADMIN_CLIENT}/user-app"
private const val ALL_USER_APP_PAGEABLE = "${USER_APP}-pageable"

private const val CREATE_USER_APP = "${USER_APP}/create"
private const val UPDATE_USER_APP = "${USER_APP}/update"
private const val DELETE_USER_APP = "${USER_APP}/delete"


fun Route.appsAdminRoute() {
    val appsAdminDataSource: AppsAdminDataSource by inject()
    val appsUserDataSource: AppsUserDataSource by inject()

    authenticate {

        /**
         * ADMIN APP
         */
        get(ALL_ADMIN_APP_PAGEABLE) {
            logger.debug { "GET ALL /$ALL_ADMIN_APP_PAGEABLE" }

            try {
                val params = call.request.queryParameters
                val appsOption = getAppsOptions(params)
                val appsList =
                    appsAdminDataSource
                        .getAllAppsPageable(
                            query = appsOption.query,
                            page = appsOption.page!!,
                            perPage = appsOption.perPage!!,
                            sortField = appsOption.sortFiled!!,
                            sortDirection = appsOption.sortDirection!!
                        )
                if (appsList.isEmpty()) throw NotFoundException("no apps is found.")
                val numberOfNews = appsAdminDataSource.getNumberOfApps()

                respondWithSuccessfullyResult(
                    result = MyResponsePageable(
                        page = appsOption.page + 1,
                        perPage = numberOfNews,
                        data = appsList
                    ),
                    message = "get all apps successfully"
                )
            } catch (e: Exception) {
                logger.error { "${e.stackTrace ?: "An unknown error occurred"}" }
                throw UnknownErrorException(e.message ?: "An unknown error occurred  ")
            }


        }


        //get all -> api/v1/admin-client/admin-app/{id}
        get("$ADMIN_APP/{id}") {
            logger.debug { "get $ADMIN_APP/{id}" }
            try {
                call.parameters["id"]?.toIntOrNull()?.let { id ->

                    appsAdminDataSource
                        .getAppInfoByIdDto(appId = id)?.let { app ->
                            respondWithSuccessfullyResult(
                                result = app,
                                message = "get app successfully"
                            )
                        } ?: throw NotFoundException("app is not found")

                } ?: throw MissingParameterException("Missing parameters .")

            } catch (e: Exception) {
                logger.error { "get by id app error ${e.stackTrace}" }
                throw UnknownErrorException(e.message ?: "An Known Error Occurred ")


            }

        }
        //post  -> api/v1/admin-client/admin-app/create
        post(CREATE_ADMIN_APP) {
            logger.debug { "create new app $CREATE_ADMIN_APP" }


            try {//check if this app is inserted before
                val appRequest = call.receive<AppCreateDto>()
                val adminUserId = extractAdminId()
                val createdApp = appsAdminDataSource
                    .addApp(appRequest.toEntity(adminId = adminUserId))
                respondWithSuccessfullyResult(
                    statusCode = HttpStatusCode.OK,
                    result = createdApp,
                    message = "App Information inserted successfully ."
                )

            } catch (exc: Exception) {
                logger.error { "$CREATE_ADMIN_APP error is ${exc.stackTrace}" }
                throw UnknownErrorException(exc.message ?: "An Known Error Occurred ")

            }

        }
        //put  -> api/v1/admin-client/admin-app/update
        put("$UPDATE_ADMIN_APP/{id}") {
            logger.debug { "update apps $UPDATE_ADMIN_APP" }

            try {
                call.parameters["id"]?.toIntOrNull()?.let { id ->
                    val appRequest = call.receive<AppCreateDto>()

                    val adminUserId = extractAdminId()
                    if (appsAdminDataSource.getAppInfoByPackage(appRequest.packageName) != null)
                        throw AlreadyExistsException("that package name (${appRequest.packageName}) is already found ")
                    val updatedApp = appsAdminDataSource
                        .appUpdate(id, appRequest.toEntity(adminId = adminUserId))
                    if (updatedApp > 0) {
                        logger.debug { "app info save successfully in db" }
                        val updateApp =
                            appsAdminDataSource
                                .getAppInfoByPackageDto(appRequest.packageName)
                                ?: throw NotFoundException("package name (${appRequest.packageName}) is not found ")

                        respondWithSuccessfullyResult(
                            result = updateApp,
                            message = "app updated successfully ."
                        )
                    } else {
                        throw UnknownErrorException("update failed .")
                    }
                } ?: throw MissingParameterException("Missing parameters .")
            } catch (exc: Exception) {
                logger.error { "$CREATE_ADMIN_APP error is ${exc.stackTrace}" }
                throw UnknownErrorException(exc.message ?: "An Known Error Occurred ")

            }


        }
        //delete  -> api/v1/admin-client/admin-app/delete
        delete("$DELETE_ADMIN_APP/{id}") {
            logger.debug { "delete app $DELETE_ADMIN_APP" }
            try {
                call.parameters["id"]?.toIntOrNull()?.let { id ->

                    val adminApp = appsAdminDataSource.appDelete(appId = id)
                    if (adminApp > 0) {
                        respondWithSuccessfullyResult(
                            result = true,
                            message = "App deleted successfully ."
                        )
                    } else {
                        throw UnknownErrorException("failed to delete app .")
                    }
                } ?: throw MissingParameterException("Missing parameters .")
            } catch (exc: Exception) {
                logger.error { "$DELETE_ADMIN_APP error ${exc.stackTrace ?: "An unknown error occurred"}" }

                throw UnknownErrorException(exc.message ?: "An Known Error Occurred .")

            }


        }


        /**
         * USER APP
         */
        get(ALL_USER_APP_PAGEABLE) {
            logger.debug { "GET ALL /$ALL_USER_APP_PAGEABLE" }

            try {
                val params = call.request.queryParameters
                val appsOption = getAppsOptions(params)
                val appsList =
                    appsUserDataSource
                        .getAllAppsPageable(
                            query = appsOption.query,
                            page = appsOption.page!!,
                            perPage = appsOption.perPage!!,
                            sortField = appsOption.sortFiled!!,
                            sortDirection = appsOption.sortDirection!!
                        )
                if (appsList.isEmpty()) throw NotFoundException("no apps is found.")
                val numberOfNews = appsUserDataSource.getNumberOfApps()

                respondWithSuccessfullyResult(
                    result = MyResponsePageable(
                        page = appsOption.page + 1,
                        perPage = numberOfNews,
                        data = appsList
                    ),
                    message = "get all apps successfully"
                )
            } catch (e: Exception) {
                logger.error { "${e.stackTrace ?: "An unknown error occurred"}" }
                throw UnknownErrorException(e.message ?: "An unknown error occurred  ")
            }


        }

        //get all -> api/v1/admin-client/user-app/{id}
        get("$USER_APP/{id}") {
            logger.debug { "get user App Client $USER_APP/{id}" }
            try {
                call.parameters["id"]?.toIntOrNull()?.let { id ->

                    appsUserDataSource
                        .getAppInfoByIdDto(appId = id)?.let { app ->
                            logger.debug { "get user App successfully $app" }

                            respondWithSuccessfullyResult(
                                result = app,
                                message = "get app successfully"
                            )
                        } ?: throw NotFoundException("app is not found")

                } ?: throw MissingParameterException("Missing parameters .")

            } catch (e: Exception) {
                logger.error { "get by id app error ${e.stackTrace}" }
                throw UnknownErrorException(e.message ?: "An Known Error Occurred ")


            }

        }
        //post  -> api/v1/admin-client/user-app/create
        post(CREATE_USER_APP) {
            logger.debug { "create new user app $CREATE_USER_APP" }

            try {//check if this app is inserted before
                val appRequest = call.receive<AppCreateDto>()
                val adminUserId = extractAdminId()
                val createdApp = appsUserDataSource
                    .addApp(appRequest.toEntity(adminId = adminUserId))
                respondWithSuccessfullyResult(
                    statusCode = HttpStatusCode.OK,
                    result = createdApp,
                    message = "App Information inserted successfully ."
                )

            } catch (exc: Exception) {
                logger.error { "$CREATE_ADMIN_APP error is ${exc.stackTrace}" }
                throw UnknownErrorException(exc.message ?: "An Known Error Occurred ")

            }


        }
        //put  -> api/v1/admin-client/user-app/update
        put("$UPDATE_USER_APP/{id}") {
            logger.debug { "update USER apps $UPDATE_USER_APP" }

            try {
                call.parameters["id"]?.toIntOrNull()?.let { id ->
                    val userAppRequest = call.receive<AppCreateDto>()

                    val adminUserId = extractAdminId()
                    if (appsUserDataSource.getAppInfoByPackage(userAppRequest.packageName) != null)
                        throw AlreadyExistsException("that package name (${userAppRequest.packageName}) is already found ")
                    val updatedApp = appsUserDataSource
                        .appUpdate(id, userAppRequest.toEntity(adminId = adminUserId))
                    if (updatedApp > 0) {
                        logger.debug { "app info save successfully in db" }
                        val updateUserApp =
                            appsUserDataSource
                                .getAppInfoByPackageDto(userAppRequest.packageName)
                                ?: throw NotFoundException("package name (${userAppRequest.packageName}) is not found ")

                        respondWithSuccessfullyResult(
                            result = updateUserApp,
                            message = "app updated successfully ."
                        )
                    } else {
                        throw UnknownErrorException("update failed .")
                    }
                } ?: throw MissingParameterException("Missing parameters .")
            } catch (exc: Exception) {
                logger.error { "$CREATE_ADMIN_APP error is ${exc.stackTrace}" }
                throw UnknownErrorException(exc.message ?: "An Known Error Occurred ")

            }


        }
        //delete  -> api/v1/admin-client/app/delete
        delete("$DELETE_USER_APP/{id}") {
            logger.debug { "delete app $DELETE_USER_APP" }
            try {
                call.parameters["id"]?.toIntOrNull()?.let { id ->

                    val result = appsUserDataSource.appDelete(appId = id)
                    if (result > 0) {
                        respondWithSuccessfullyResult(
                            result = true,
                            message = "App deleted successfully ."
                        )
                    } else {
                        throw UnknownErrorException("failed to delete App .")
                    }
                } ?: throw MissingParameterException("Missing parameters .")
            } catch (exc: Exception) {
                logger.error { "$DELETE_USER_APP error ${exc.stackTrace ?: "An unknown error occurred"}" }
                throw UnknownErrorException(exc.message ?: "An Known Error Occurred .")

            }


        }
    }


}