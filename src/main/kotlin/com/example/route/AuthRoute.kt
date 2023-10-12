package com.example.route

import com.example.data.admin_user.UserDataSource
import com.example.models.AdminUser
import com.example.models.request.AdminRegister
import com.example.models.request.UserRequest
import com.example.security.hash.HashingService
import com.example.security.hash.SaltedHash
import com.example.security.token.TokenClaim
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import com.example.utils.MyResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import java.time.LocalDateTime


const val ENDPOINT = "/api/v1"
const val USERS = "$ENDPOINT/users"
const val REGISTER_REQUEST = "$USERS/register"
const val LOGIN_REQUEST = "$USERS/login"
const val ME_REQUEST = "$USERS/me"

private val logger = KotlinLogging.logger {}

fun Route.adminUsers(
    userDataSource: UserDataSource,

    ) {
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
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    config: TokenConfig
) {
    // Login a user --> POST /api/v1/users/login
    post(LOGIN_REQUEST) {
        logger.debug { "POST /$LOGIN_REQUEST" }
        // check body request if  missing some fields
        val loginRequest = try {
            call.receive<UserRequest>()
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.OK,
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
            val adminUser = userDataSource.getUserByUsername(loginRequest.username)
            if (adminUser != null) {
                val isValidPassword = hashingService.verifyHashingPassword(
                    value = loginRequest.password,
                    saltedHash = SaltedHash(hash = adminUser.password, salt = adminUser.salt)
                )
                if (isValidPassword) {
                    val token = tokenService.generateToken(
                        config = config,
                        TokenClaim(
                            name = "userId",
                            value = adminUser.id.toString()
                        )
                    )
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "You are logged in successfully",
                            data = token
                        )
                    )
                    return@post

                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = false,
                            message = "Password Incorrect",
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
                        message = "Email is wrong",
                        data = null
                    )
                )
                return@post
            }

        } catch (exc: Exception) {
            call.respond(
                HttpStatusCode.OK,
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
        // Get the user info --> GET /api/users/me (with token)
        get(ME_REQUEST) {
            logger.debug { "get /$ME_REQUEST" }

            val principal = call.principal<JWTPrincipal>()
            val userId = try {
                principal?.getClaim("userId", String::class)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@get
            }
            call.respond(HttpStatusCode.OK, "Your userId is $userId")

        }
    }
}

fun Route.register(
    userDataSource: UserDataSource,
    hashingService: HashingService,
) {
    //base_url/v1/users/register
    post(REGISTER_REQUEST) {
        // check body request if  missing some fields
        val registerRequest = try {
            call.receive<AdminRegister>()
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.OK,
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
                    created_at = LocalDateTime.now().toString(),
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
                HttpStatusCode.OK,
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

