package com.example.mapper

import com.example.models.CeramicProductInfo
import com.example.models.Product
import com.example.models.dto.CeramicCreateDto
import com.example.models.dto.ProductDto
import com.example.models.dto.ProductUserClient
import com.example.models.dto.ProductUserClientDto

fun CeramicCreateDto.toEntity(adminId: Int) =
    CeramicProductInfo(
        typeCategoryId = typeCategoryId,
        sizeCategoryId = sizeCategoryId,
        colorCategoryId = colorCategoryId,
        productName = productName,
        productImageUrl = productImageUrl,
        userAdminId = adminId
    )


fun ProductDto.toClientDto() = ProductUserClientDto(
    id, typeCategoryName, sizeCategoryName, colorCategoryName, productName, image, isHot
)

fun List<ProductDto>.toClientDto() = map { it.toClientDto() }



fun Product.toModelResponse() = ProductUserClient(
    id, typeCategoryId, sizeCategoryId, colorCategoryId, productName, image,  isHot
)

fun List<Product>.toModelResponse() = map { it.toModelResponse() }