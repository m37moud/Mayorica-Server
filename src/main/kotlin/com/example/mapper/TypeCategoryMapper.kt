package com.example.mapper

import com.example.models.TypeCategoryInfo
import com.example.models.dto.TypeCategoryCreateDto

fun TypeCategoryCreateDto.toEntity(adminId: Int) =
    TypeCategoryInfo(
        typeName = typeName,
        iconUrl = iconUrl,
        userAdminId = adminId
    )