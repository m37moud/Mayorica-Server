package com.example.mapper

import com.example.models.News
import com.example.models.NewsCreate
import com.example.models.SizeCategory
import com.example.models.dto.NewsCreateDto
import com.example.models.response.NewsUserClientResponse
import com.example.models.response.SizeCategoryUserClientResponse

fun NewsCreateDto.toEntity(adminId: Int) =
    NewsCreate(
        newsTitle = newsTitle,
        newsDescription = newsDescription,
        newsImageUrl = newsImageUrl,
        userAdminId = adminId
    )


fun News.toUserResponse() = NewsUserClientResponse(
    id, title, image, newsDescription,createdAt
)

fun List<News>.toUserResponse() = map { it.toUserResponse() }