package com.example.plugins

import com.example.utils.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        handleStatusPageExceptions()
//        exception<Throwable> { call, cause ->
//            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
//        }
    }
}

private fun StatusPagesConfig.handleStatusPageExceptions() {
    respondWithErrorCodes<MissingParameterException>(HttpStatusCode.BadRequest)

    respondWithErrorCodes<RequestValidationException>(HttpStatusCode.BadRequest)

    respondWithErrorCodes<UserAlreadyExistsException>(HttpStatusCode.UnprocessableEntity)

    respondWithErrorCodes<ResourceNotFoundException>(HttpStatusCode.NotFound)

    respondWithErrorCodes<InsufficientFundsException>(HttpStatusCode.UnprocessableEntity)

    respondWithErrorCodes<InvalidCredentialsException>(HttpStatusCode.Unauthorized)

    respondWithErrorCodes<InvalidLocationException>(HttpStatusCode.BadRequest)

    respondWithErrorCodes<AlreadyExistsException>(HttpStatusCode.BadRequest)

    respondWithErrorCodes<UnknownErrorException>(HttpStatusCode.BadRequest)
    respondWithErrorCodes<ErrorException>(HttpStatusCode.BadRequest)
    respondWithErrorCodes<UploadImageException>(HttpStatusCode.InternalServerError)
    respondWithErrorCodes<DeleteImageException>(HttpStatusCode.InternalServerError)
    respondWithErrorCodes<NotFoundException>(HttpStatusCode.NotFound)
}

private inline fun <reified T : Throwable> StatusPagesConfig.respondWithErrorCodes(
    statusCode: HttpStatusCode
) {
    exception<T> { call, t ->
        println("Status Code = ${t.message}")

        val reasons = t.message?.split(",") ?: emptyList()
        println("Status reasons = $reasons")

        call.respond(
            status = statusCode,
            MyResponse(
                success = false,
                message = t.message ?: "Some thing goes wrong .",
                data = null
            )
        )

    }

}