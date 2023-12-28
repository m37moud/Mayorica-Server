package com.example.route.client_admin_side

import com.example.data.administrations.admin_user.UserDataSource
import com.example.database.table.AdminUserEntity
import com.example.models.AdminUser
import com.example.models.AdminUserDetail
import com.example.models.MyResponsePageable
import com.example.models.request.auth.AdminRegister
import com.example.models.request.auth.LoginRequest
import com.example.models.response.UserAdminResponse
import com.example.security.hash.HashingService
import com.example.security.hash.SaltedHash
import com.example.security.token.TokenClaim
import com.example.security.token.TokenService
import com.example.utils.Claim.PERMISSION
import com.example.utils.Claim.USERNAME
import com.example.utils.Claim.USER_ID
import com.example.utils.Constants.ADMIN_CLIENT
import com.example.utils.MyResponse
import com.example.utils.toDatabaseString
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

private const val USERS = "$ADMIN_CLIENT/users"
private const val REGISTER_REQUEST = "$USERS/register"
private const val LOGIN_REQUEST = "$USERS/login"
private const val ME_REQUEST = "$USERS/me"

private val logger = KotlinLogging.logger {}

fun Route.authenticationRoutes(
//    config: TokenConfig
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
                if (userDataSource.getUserByUsername(registerRequest.username) == null) // means not found
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
                        call.respond(
                            HttpStatusCode.OK,
                            MyResponse(
                                success = true,
                                message = "Registration Successfully",
                                data = null
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
    }
    //get al users
    get(USERS) {
        logger.debug { "get /$USERS" }
        val params = call.request.queryParameters
        params["page"]?.toIntOrNull()?.let { pageNum ->
            val page = if (pageNum > 0) pageNum else 0
            val perPage = params["perPage"]?.toIntOrNull() ?: 10
            val sortFied = when (params["sort_by"] ?: "date") {
                "name" -> AdminUserEntity.full_name
                "date" -> AdminUserEntity.created_at
                else -> {
                    return@get call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = MyResponse(
                            success = false,
                            message = "invalid parameter for sort_by chose between (name & date)",
                            data = null
                        )
                    )
                }
            }
            val sortDirection = when (params["sort_direction"] ?: "dec") {
                "dec" -> -1
                "asc" -> 1
                else -> {
                    return@get call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = MyResponse(
                            success = false,
                            message = "invalid parameter for sort_direction chose between (dec & asc)",
                            data = null
                        )
                    )
                }
            }
            logger.debug { "GET ALL User /$USERS?page=$page&perPage=$perPage" }
            val query: String? = params["query"]?.trim()
            val permission: String? = params["permission"]?.trim()
            val users = try {
                userDataSource.getAllUserPageable(
                    query = query,
                    permission = permission,
                    page = page,
                    perPage = perPage,
                    sortField = sortFied,
                    sortDirection = sortDirection

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
            if (users.isNotEmpty()) {
                call.respond(
                    HttpStatusCode.OK, MyResponse(
                        success = true,
                        message = "get all users successfully",
                        data = MyResponsePageable(page = page, perPage = perPage, data = users)
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.OK, MyResponse(
                        success = false,
                        message = "no user is found",
                        data = null
                    )
                )
            }


        } ?: run {
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
        }


    }
    // Login a user --> POST /api/v1/admin-client/users/login
    post(LOGIN_REQUEST) {
        logger.debug { "POST /$LOGIN_REQUEST" }
        // check body request if  missing some fields
//        val loginRequest = try {
//            call.receive<LoginRequest>()
//        } catch (e: Exception) {
//            call.respond(
//                HttpStatusCode.Conflict,
//                MyResponse(
//                    success = false,
//                    message = e.message ?: "Missing Some Fields",
//                    data = null
//                )
//            )
//            return@post
//        }
//        val username = call.request.queryParameters["username"]
//        val password = call.request.queryParameters["password"]
        val params = call.receiveParameters()
        val username = params["username"]?.trim().toString()
        val password = params["password"]?.trim().toString()

        logger.debug { "username /$username , password = $password " }

        // check if operation connected db successfully
        try {
            val adminUser = userDataSource.getUserByUsername(
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
//                    val token = tokenService.generateToken(
////                        config = config,
//                        TokenClaim(
//                            name = "userId",
//                            value = adminUser.id.toString()
//                        ),
//                        TokenClaim(
//                            name = "userRole",
//                            value = adminUser.role.toString()
//                        )
//
//                    )
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "You are logged in successfully",
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
    // me
    authenticate {
        // Get the user info --> GET /api/v1/users/me (with token)
        get(ME_REQUEST) {
            logger.debug { "get /$ME_REQUEST" }

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
                return@get
            }
            val userRole = try {
                principal?.getClaim("userRole", String::class)
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
    }

}

fun Route.adminUsers(
//    userDataSource: UserDataSource,

) {
    val userDataSource: UserDataSource by inject()

    get(USERS) {
        logger.debug { "get /$USERS" }
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
        }
        call.respond(
            HttpStatusCode.OK, MyResponse(
                success = true,
                message = "admins user is found .",
                data = users
            )
        )

    }
}

fun Route.login(
//    userDataSource: UserDataSource,
//    hashingService: HashingService,
//    tokenService: TokenService,
//    config: TokenConfig
) {
    val userDataSource: UserDataSource by inject()
    val hashingService: HashingService by inject()
    val tokenService: TokenService by inject()

    // Login a user --> POST /api/v1/admin-client/users/login
    post(LOGIN_REQUEST) {
        logger.debug { "POST /$LOGIN_REQUEST" }
        // check body request if  missing some fields
        val loginRequest = try {
            call.receive<LoginRequest>()
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                MyResponse(
                    success = false,
                    message = e.message ?: "Missing Some Fields",
                    data = null
                )
            )
            return@post
        }

        // check if operation connected db successfully
        try {
            val adminUser = userDataSource.getUserByUsername(loginRequest.username)
            if (adminUser != null) {
                val isValidPassword = hashingService.verifyHashingPassword(
                    value = loginRequest.password,
                    saltedHash = SaltedHash(hash = adminUser.password, salt = adminUser.salt)
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
//                    val token = tokenService.generateToken(
////                        config = config,
//                        TokenClaim(
//                            name = "userId",
//                            value = adminUser.id.toString()
//                        ),
//                        TokenClaim(
//                            name = "userRole",
//                            value = adminUser.role.toString()
//                        )
//
//                    )
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "You are logged in successfully",
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

fun Route.getSecretInfo() {


    authenticate {
        // Get the user info --> GET /api/v1/users/me (with token)
        get(ME_REQUEST) {
            logger.debug { "get /$ME_REQUEST" }

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
                return@get
            }
            val userRole = try {
                principal?.getClaim("userRole", String::class)
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
    }

}

fun Route.register(
//    userDataSource: UserDataSource,
//    hashingService: HashingService,
) {
    val userDataSource: UserDataSource by inject()
    val hashingService: HashingService by inject()

    //base_url/api/v1/admin-client/users/register
    authenticate("app") {

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
                if (userDataSource.getUserByUsername(registerRequest.username) == null) // means not found
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
                        call.respond(
                            HttpStatusCode.OK,
                            MyResponse(
                                success = true,
                                message = "Registration Successfully",
                                data = null
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
    }
}


