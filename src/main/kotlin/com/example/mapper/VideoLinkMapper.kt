package com.example.mapper

import com.example.models.VideoLinkCreate
import com.example.models.dto.VideoLinkCreateDto

fun VideoLinkCreateDto.toModel(adminId: Int) = VideoLinkCreate(
    idLink, linkEnabled, adminId
)