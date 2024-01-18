package com.example.mapper

import com.example.models.SizeCategoryInfo
import com.example.models.dto.SizeCategoryCreateDto


fun SizeCategoryCreateDto.toEntity(adminId: Int) =
    SizeCategoryInfo(
        typeId = typeCategoryId,
        sizeName = sizeName,
        sizeImageUrl = sizeImageUrl,
        userAdminId = adminId
    )