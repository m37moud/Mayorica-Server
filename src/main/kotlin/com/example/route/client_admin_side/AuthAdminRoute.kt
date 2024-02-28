package com.example.route.client_admin_side

import com.example.data.administrations.admin_user.UserDataSource
import com.example.database.table.AdminUserEntity
import com.example.models.AdminUser
import com.example.models.MyResponsePageable
import com.example.models.options.getAdminUserOptions
import com.example.models.options.getAppsOptions
import com.example.models.options.getUserOptions
import com.example.models.request.auth.AdminRegister
import com.example.models.response.UserAdminResponse
import com.example.security.hash.HashingService
import com.example.security.hash.SaltedHash
import com.example.security.token.TokenClaim
import com.example.security.token.TokenService
import com.example.utils.*
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
private const val UPDATE_USER_INFO_REQUEST = "$USER/update"
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

            // check body request if  missing some fields
            val registerRequest = try {
                call.receive<AdminRegister>()
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

            // check if operation connected db successfully
            try {
                if (registerRequest.username.isEmpty() || registerRequest.password.isEmpty() || registerRequest.role.isEmpty()) {
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

            val principal = call.principal<JWTPrincipal>()
            val userId = try {
                principal?.getClaim(USER_ID, String::class)?.toIntOrNull()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@get
            }
            val userRole = try {
                principal?.getClaim(PERMISSION, String::class)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@get
            }

            call.respond(
                HttpStatusCode.OK, MyResponse(
                    success = true,
                    message = "",
                    data = UserAdminResponse(
                        id = userId!!,
                        role = userRole!!
                    )
                )
            )

        }
        // delete the user info --> delete /api/v1/user/delete (with token)
        delete("$DELETE_REQUEST/{id}") {
            logger.debug { "delete /$DELETE_REQUEST/{id}" }
            call.parameters["id"]?.toIntOrNull()?.let { id ->
                try {
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
    }

    // Login a user --> POST /api/v1/admin-client/users/login
    post(LOGIN_REQUEST) {
        logger.debug { "POST /$LOGIN_REQUEST" }
        // check body request if  missing some fields
        val params = call.receiveParameters()
        val username = params["username"]?.trim().toString()
        val password = params["password"]?.trim().toString()

        logger.debug { "username /$username , password = $password " }

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
                            name = USERNAME,
                            value = adminUser.username
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

