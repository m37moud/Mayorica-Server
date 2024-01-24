package com.example.mapper

import com.example.models.CeramicProductInfo
import com.example.models.dto.CeramicCreateDto

fun CeramicCreateDto.toEntity(adminId: Int) =
    CeramicProductInfo(
        typeCategoryId = typeCategoryId,
        sizeCategoryId = sizeCategoryId,
        colorCategoryId = colorCategoryId,
        productName = productName,
        productImageUrl = productImageUrl,
        userAdminId = adminId
    )