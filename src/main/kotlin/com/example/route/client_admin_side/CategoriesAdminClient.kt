package com.example.route.client_admin_side

import com.example.mapper.toModelCreate
import com.example.models.request.categories.TypeCategoryRequest
import com.example.utils.MyResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging

const val CATEGORIES = "$ADMIN_CLIENT/categories"
const val CATEGORY = "$ADMIN_CLIENT/category"
const val CREATE_TYPE_CATEGORY = "$CATEGORY/create"
private val logger = KotlinLogging.logger {}

fun Route.categories() {
    authenticate {
        post(CREATE_TYPE_CATEGORY) {
            val typeCategoryRequest = try {
                call.receive<TypeCategoryRequest>()
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
            val principal = call.principal<JWTPrincipal>()
            val userId = try {
                principal?.getClaim("userId", String::class)?.toIntOrNull()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@post
            }

            typeCategoryRequest.toModelCreate(userId!!)


        }

    }
}