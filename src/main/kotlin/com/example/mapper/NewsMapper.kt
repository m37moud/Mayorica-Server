package com.example.mapper

import com.example.models.NewsCreate
import com.example.models.dto.NewsCreateDto

fun NewsCreateDto.toEntity(adminId: Int) =
    NewsCreate(
        newsTitle = newsTitle,
        newsDescription = newsDescription,
        newsImageUrl = newsImageUrl,
        userAdminId = adminId
    )