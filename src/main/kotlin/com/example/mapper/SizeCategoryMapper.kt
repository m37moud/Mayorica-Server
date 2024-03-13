package com.example.mapper

import com.example.models.SizeCategory
import com.example.models.SizeCategoryInfo
import com.example.models.TypeCategory
import com.example.models.dto.SizeCategoryCreateDto
import com.example.models.response.SizeCategoryUserClientResponse
import com.example.models.response.TypeCategoryUserClientResponse


fun SizeCategoryCreateDto.toEntity(adminId: Int) =
    SizeCategoryInfo(
        typeId = typeCategoryId,
        sizeName = sizeName,
        sizeImageUrl = sizeImageUrl,
        userAdminId = adminId
    )

fun SizeCategory.toUserResponse() = SizeCategoryUserClientResponse(
    id, typeCategoryId, size, sizeImage
)
fun List<SizeCategory>.toUserResponse() =map { it.toUserResponse() }