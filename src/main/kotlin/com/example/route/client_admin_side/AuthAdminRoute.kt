package com.example.route.client_admin_side

import com.example.data.administrations.admin_user.UserDataSource
import com.example.mapper.toEntity
import com.example.models.AdminUser
import com.example.models.MyResponsePageable
import com.example.models.UpdateUserPasswordInfo
import com.example.models.dto.UpdateUserPasswordInfoDto
import com.example.models.dto.UpdateUserProfileInfoDto
import com.example.models.mapper.toDto
import com.example.models.options.getAdminUserOptions
import com.example.models.request.auth.AdminRegister
import com.example.models.response.UserAdminResponse
import com.example.security.hash.HashingService
import com.example.security.hash.SaltedHash
import com.example.security.token.TokenClaim
import com.example.security.token.TokenService
import com.example.utils.*
import com.example.utils.Claim.CREATED_AT
import com.example.utils.Claim.FULL_NAME
import com.example.utils.Claim.PERMISSION
import com.example.utils.Claim.USERNAME
import com.example.utils.Claim.USER_ID
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
import java.time.LocalDateTime
import java.util.*

private const val USERS = "$ADMIN_CLIENT/users"
private const val USERS_PAGEABLE = "$USERS-pageable"
private const val USER = "$ADMIN_CLIENT/user"
private const val REGISTER_REQUEST = "$USER/register"
private const val DELETE_REQUEST = "$USER/delete"
private const val UPDATE_USER_INFO_REQUEST = "$USER/updateInfo"
private const val UPDATE_USER_PASS_REQUEST = "$USER/updatePassword"
private const val UPDATE_USER_PERMISSION_REQUEST = "$UPDATE_USER_INFO_REQUEST/permission"
private const val LOGIN_REQUEST = "$USER/login"
private const val ME_REQUEST = "$USERS/me"

private val logger = KotlinLogging.logger {}

fun Route.authenticationRoutes(
) {
    val userDataSource: UserDataSource by inject()
    val hashingService: HashingService by inject()
    val tokenService: TokenService by inject()

    //register new user
    authenticate {
        post(REGISTER_REQUEST) {


            try {
                val userAdminId = extractAdminId()

                val isAdmin = userDataSource.isAdmin(userAdminId)
                if (isAdmin) {
                    // check body request if  missing some fields
                    val registerRequest = call.receive<AdminRegister>()

                    // check if operation connected db successfully
                    if (registerRequest.username.isEmpty() || registerRequest.password.isEmpty() || registerRequest.role.isEmpty()) {
                        throw UnknownErrorException("Failed Registration")
                    }

                    // check if email exist or note
                    if (userDataSource.getUserDetailByUsername(registerRequest.username) == null) // means not found
                    {
                        val saltedHash = hashingService.createHashingPassword(registerRequest.password)
                        val user = AdminUser(
                            username = registerRequest.username,
                            full_name = registerRequest.full_name,
                            password = saltedHash.hash,
                            salt = saltedHash.salt,
                            role = registerRequest.role,
                            created_at = LocalDateTime.now().toDatabaseString(),
                            updated_at = ""
                        )

                        val result = userDataSource.register(user)
                        // if result >0 it's success else is failed
                        if (result > 0) {
                            val adminUser = userDataSource.getUserDetailByUsername(user.username)

                            call.respond(
                                HttpStatusCode.OK,
                                MyResponse(
                                    success = true,
                                    message = "Registration Successfully",
                                    data = adminUser
                                )
                            )
                            return@post
                        } else {
                            call.respond(
                                HttpStatusCode.OK,
                                MyResponse(
                                    success = false,
                                    message = "Failed Registration",
                                    data = null
                                )
                            )
                            return@post
                        }
                    } else {
                        call.respond(
                            HttpStatusCode.OK,
                            MyResponse(
                                success = false,
                                message = "User already registration before.",
                                data = null
                            )
                        )
                        return@post
                    }
                } else {
                    throw UnknownErrorException("You Are Not Authorize .")
                }


            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed Registration",
                        data = null
                    )
                )
                return@post
            }

        }
        //get all pageable users
        get(USERS_PAGEABLE) {
            logger.debug { "get all pageable users /$USERS_PAGEABLE" }

            try {
                val params = call.request.queryParameters
                val appsOption = getAdminUserOptions(params)
                logger.debug { "GET ALL User /$USERS?page=${appsOption.page}&perPage=${appsOption.perPage}" }

                val users = userDataSource.getAllUserPageable(
                    query = appsOption.query,
                    permission = appsOption.permission,
                    page = appsOption.page!!,
                    perPage = appsOption.perPage!!,
                    sortField = appsOption.sortFiled!!,
                    sortDirection = appsOption.sortDirection!!,

                    )
                if (users.isEmpty()) throw NotFoundException("users is not found")
                val numberOfUsers = userDataSource.getNumberOUsers()
                respondWithSuccessfullyResult(
                    result = MyResponsePageable(
                        page = appsOption.page + 1,
                        perPage = numberOfUsers,
                        data = users
                    ),
                    message = "get all users successfully"
                )


            } catch (e: Exception) {
                logger.error { "${e.stackTrace ?: "An unknown error occurred"}" }
                throw UnknownErrorException(e.message ?: "An unknown error occurred  ")

            }


        }
        //get all users
        get(USERS) {
            logger.debug { "get all users /$USERS_PAGEABLE" }

            try {
                val users = userDataSource.getAllUser()
                if (users.isEmpty()) {
                    call.respond(
                        HttpStatusCode.OK, MyResponse(
                            success = false,
                            message = "no admins user is found .",
                            data = null
                        )
                    )
                    return@get
                } else {
                    call.respond(
                        HttpStatusCode.OK, MyResponse(
                            success = true,
                            message = "admins user is found .",
                            data = users
                        )
                    )
                    return@get
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

        // me
        // Get the user info --> GET /api/v1/user/me (with token)
        get(ME_REQUEST) {
            logger.debug { "get /$ME_REQUEST" }

            try {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim(USER_ID, String::class)?.toIntOrNull()

                val userDetails =
                    userDataSource.getUserDetailById(userId!!) ?: throw NotFoundException("User Not Found ")


                logger.debug { "userId = $userId" }
                logger.debug { "userRole = ${userDetails.role}" }
                logger.debug { "fullName = ${userDetails.full_name}" }
                logger.debug { "userName = ${userDetails.username}" }
                logger.debug { "createdAt = ${userDetails.created_at}" }
                logger.debug { "user  = ${userDetails.toDto(userId)}" }


                respondWithSuccessfullyResult(
                    result = userDetails.toDto(userId),
                    message = "Get User Profile Successfully"
                )

            } catch (e: Exception) {
                logger.error { "${e.stackTrace ?: "An unknown error occurred"}" }
                throw UnknownErrorException(e.message ?: "An unknown error occurred  ")

            }


        }
        // delete the user info --> delete /api/v1/user/delete (with token)
        delete("$DELETE_REQUEST/{id}") {
            logger.debug { "delete /$DELETE_REQUEST/{id}" }
            call.parameters["id"]?.toIntOrNull()?.let { id ->
                try {
                    val userAdminId = extractAdminId()

                    val isAdmin = userDataSource.isAdmin(userAdminId)
                    if (!isAdmin)
                        throw UnknownErrorException("You Are Not Authorize .")

                    val result = userDataSource.deleteAdminUser(id = id)
                    if (result > 0) {
                        call.respond(
                            HttpStatusCode.OK,
                            MyResponse(
                                success = true,
                                message = "User deleted successfully .",
                                data = null
                            )
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            MyResponse(
                                success = false,
                                message = " User deleted failed .",
                                data = null
                            )
                        )
                        return@delete
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.Conflict,
                        MyResponse(
                            success = false,
                            message = e.message ?: "Failed ",
                            data = null
                        )
                    )
                    return@delete
                }

            } ?: call.respond(
                status = HttpStatusCode.OK, message = MyResponse(
                    success = false,
                    message = "M",
                    data = null
                )
            )

        }
        // put update user permission info --> put /api/v1/user/delete (with token)
        put("$UPDATE_USER_PERMISSION_REQUEST/{id}") {
            logger.debug { "Put -> $UPDATE_USER_PERMISSION_REQUEST" }
            call.parameters["id"]?.toIntOrNull()?.let { id ->
                val permission = call.parameters["permission"]?.trim()?.uppercase(Locale.getDefault())
                try {
                    val result = userDataSource.updatePermission(id = id, permission = permission!!)
                    if (result > 0) {
                        userDataSource.getUserDetailById(id)?.let { user ->

                            call.respond(
                                status = HttpStatusCode.OK,
                                message = MyResponse(
                                    success = true,
                                    message = "Permission Updated Successfully .",
                                    data = user
                                )
                            )
                            return@put
                        }
                    } else {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = MyResponse(
                                success = false,
                                message = "Permission Updated Failed .",
                                data = null
                            )
                        )
                        return@put

                    }
                } catch (e: Exception) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = MyResponse(
                            success = false,
                            message = e.message ?: "Failed .",
                            data = null
                        )
                    )
                    return@put
                }

            }

        }

        put("$UPDATE_USER_INFO_REQUEST/{id}") {
            logger.debug { "Put -> $UPDATE_USER_INFO_REQUEST" }

            try {
                call.parameters["id"]?.toIntOrNull()?.let { id ->

                    val newUserProfileDto = call.receive<UpdateUserProfileInfoDto>()

                    val oldUser = userDataSource.getUserDetailById(id) ?: throw NotFoundException("User Not Found.")
                    val isSameUsername = newUserProfileDto.userName == oldUser.username
                    val isSameFullName = newUserProfileDto.fullName == oldUser.full_name


                    if (isSameUsername && isSameFullName) {
                        throw AlreadyExistsException("no new data to change ")
                    }
                    if (userDataSource.getAdminUserByUsername(newUserProfileDto.userName) != null && isSameFullName) {
                        throw AlreadyExistsException("that username (${newUserProfileDto.userName}) is already found ")
                    }
                    val result =
                        userDataSource.updateUserProfile(id = id, userProfileInfo = newUserProfileDto.toEntity())
                    if (result > 0) {
                        respondWithSuccessfullyResult(
                            result = true,
                            message = "User Information Updated Successfully ."
                        )
                    } else {
                        throw UnknownErrorException("update failed .")
                    }
                } ?: throw MissingParameterException("Missing parameters .")

            } catch (exc: Exception) {
                logger.error { "$UPDATE_USER_INFO_REQUEST error ${exc.stackTrace ?: "An unknown error occurred"}" }

                throw UnknownErrorException(exc.message ?: "An Known Error Occurred .")

            }


        }
        put("$UPDATE_USER_PASS_REQUEST/{id}") {
            logger.debug { "Put -> $UPDATE_USER_PASS_REQUEST" }

            try {
                call.parameters["id"]?.toIntOrNull()?.let { id ->

                    val userPasswordDto = call.receive<UpdateUserPasswordInfoDto>()
                    userDataSource.getUserDetailById(id)?.let { userDetail ->
                        val originalUse = userDataSource.getAdminUserByUsername(userDetail.username)
                            ?: throw NotFoundException("User Not Found.")
                        val isValidPassword = hashingService.verifyHashingPassword(
                            value = userPasswordDto.oldPassword,//loginRequest.password,
                            saltedHash = SaltedHash(
                                hash = originalUse.password,
                                salt = originalUse.salt
                            )
                        )
                        if (!isValidPassword)
                            throw UnknownErrorException("Wrong Password")
                        val saltedHash = hashingService.createHashingPassword(userPasswordDto.newPassword)

                        val result =
                            userDataSource
                                .updateUserPassword(
                                    id = id,
                                    userPasswordInfo = UpdateUserPasswordInfo(
                                        newPassword = saltedHash.hash,
                                        salt = saltedHash.salt
                                    )
                                )
                        if (result > 0) {
                            respondWithSuccessfullyResult(
                                result = true,
                                message = "User Password Updated Successfully ."
                            )
                        } else {
                            throw UnknownErrorException("update failed .")
                        }

                    } ?: throw NotFoundException("User Not Found.")

                } ?: throw MissingParameterException("Missing parameters .")

            } catch (exc: Exception) {
                logger.error { "$UPDATE_USER_INFO_REQUEST error ${exc.stackTrace ?: "An unknown error occurred"}" }

                throw UnknownErrorException(exc.message ?: "An Known Error Occurred .")

            }


        }

    }

    // Login a user --> POST /api/v1/admin-client/users/login
    post(LOGIN_REQUEST) {
        logger.debug { "POST /$LOGIN_REQUEST" }
        // check body request if  missing some fields
        val params = call.receiveParameters()
        val username = params["username"]?.trim().toString()
        val password = params["password"]?.trim().toString()

//        logger.debug { "username /$username , password = $password " }

        // check if operation connected db successfully
        try {
            val adminUser = userDataSource.getAdminUserByUsername(
                username //loginRequest.username
            )
            if (adminUser != null) {
                val isValidPassword = hashingService.verifyHashingPassword(
                    value = password,//loginRequest.password,
                    saltedHash = SaltedHash(
                        hash = adminUser.password,
                        salt = adminUser.salt
                    )
                )
                if (isValidPassword) {
                    val tokensResponse = tokenService.generateUserTokens(
                        TokenClaim(
                            name = USER_ID,
                            value = adminUser.id.toString()
                        ),
                        TokenClaim(
                            name = PERMISSION,
                            value = adminUser.role
                        ),
                        TokenClaim(
                            name = FULL_NAME,
                            value = adminUser.full_name
                        ),
                        TokenClaim(
                            name = USERNAME,
                            value = adminUser.username
                        ),


                        TokenClaim(
                            name = CREATED_AT,
                            value = adminUser.created_at
                        ),

                        )
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "logged in successfully",
                            data = tokensResponse
                        )
                    )
                    return@post

                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = false,
                            message = "Email or Password Incorrect",
                            data = null
                        )
                    )
                    return@post
                }

            } else {
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = false,
                        message = "you are not registered ",
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
                    message = exc.message ?: "Failed Login",
                    data = null
                )
            )
            return@post
        }


    }


}

