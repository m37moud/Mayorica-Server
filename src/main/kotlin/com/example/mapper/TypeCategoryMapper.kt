package com.example.mapper

import com.example.models.TypeCategory
import com.example.models.TypeCategoryInfo
import com.example.models.dto.TypeCategoryCreateDto
import com.example.models.response.TypeCategoryUserClientResponse

fun TypeCategoryCreateDto.toEntity(adminId: Int) =
    TypeCategoryInfo(
        typeName = typeName,
        iconUrl = iconUrl,
        userAdminId = adminId
    )

fun TypeCategory.toUserResponse() = TypeCategoryUserClientResponse(
    id = id, typeName = typeName, typeIcon = typeIcon
)
fun List<TypeCategory>.toUserResponse() =map { it.toUserResponse() }
