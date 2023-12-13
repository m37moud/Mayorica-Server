package com.example.route.auth_route

import com.example.data.administrations.admin_user.UserDataSource
import com.example.models.AdminUser
import com.example.models.request.auth.AdminRegister
import com.example.security.hash.HashingService
import com.example.utils.Constants
import com.example.utils.MyResponse
import com.example.utils.toDatabaseString
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime


private const val REGISTER = "${Constants.ADMIN_CLIENT}/register"
private const val REGISTER_ADMIN_REQUEST = "$REGISTER/admin"
fun Route.registerAdmin(
    userDataSource: UserDataSource,
    hashingService: HashingService,
){

    //base_url/api/v1/admin-client/register/admin
    authenticate("app") {
        post(REGISTER_ADMIN_REQUEST) {

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