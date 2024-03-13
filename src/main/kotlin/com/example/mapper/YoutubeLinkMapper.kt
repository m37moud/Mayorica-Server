package com.example.mapper

import com.example.models.Offer
import com.example.models.YoutubeLink
import com.example.models.request.YoutubeLinkRequest
import com.example.models.response.OfferUserClientResponse
import com.example.models.response.YoutubeLinkUserClientResponse


fun YoutubeLinkRequest.toModel(adminId :Int)  = YoutubeLink(
    idLink = this.idLink,
    userAdminId = adminId,
)

fun YoutubeLink.toUserResponse() = YoutubeLinkUserClientResponse(
    id = id, idLink = idLink, linkEnabled = linkEnabled
)
fun List<YoutubeLink>.toUserResponse() = map { it.toUserResponse() }