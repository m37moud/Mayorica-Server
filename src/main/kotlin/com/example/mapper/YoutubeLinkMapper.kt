package com.example.mapper

import com.example.models.YoutubeLink
import com.example.models.request.YoutubeLinkRequest


fun YoutubeLinkRequest.toModel(adminId :Int)  = YoutubeLink(
    idLink = this.idLink,
    userAdminId = adminId,
)