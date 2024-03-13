package com.example.mapper

import com.example.models.ColorCategory
import com.example.models.SizeCategory
import com.example.models.request.categories.ColorCategoryRequest
import com.example.models.response.ColorCategoryUserClientResponse
import com.example.models.response.SizeCategoryUserClientResponse
import com.example.utils.toDatabaseString
import java.time.LocalDateTime
//
//fun TypeCategoryRequest.toEntity(userAdminId: Int) = TypeCategory(
//    typeName = this.name,
//    typeIcon = this.typeIcon,
//    userAdminID = userAdminId,
//    createdAt = LocalDateTime.now().toDatabaseString(),
//    updatedAt = LocalDateTime.now().toDatabaseString()
//)
//
//fun SizeCategoryRequest.toEntity(userAdminId: Int) = SizeCategory(
//    typeCategoryId = this.typeCategoryId,
//    size = this.size,
//    userAdminID = userAdminId,
//    createdAt = LocalDateTime.now().toDatabaseString(),
//    updatedAt = ""
//)
//
fun ColorCategoryRequest.toEntity(userAdminId: Int) =
    ColorCategory(
        colorName = this.colorName,
        colorValue = this.colorValue,
        userAdminID = userAdminId,
        createdAt = LocalDateTime.now().toDatabaseString(),
        updatedAt = ""
    )
//
//fun TypeCategoryRequest.toModelUpdate(userAdminId: Int) = TypeCategory(
//    typeName = this.name,
//    typeIcon = this.typeIcon,
//    userAdminID = userAdminId,
//    createdAt = LocalDateTime.now().toDatabaseString(),
//    updatedAt = LocalDateTime.now().toDatabaseString(),
//)
//
//fun SizeCategoryRequest.toModelUpdate(userAdminId: Int) = SizeCategory(
//    typeCategoryId = this.typeCategoryId,
//    size = this.size,
//    userAdminID = userAdminId,
//    createdAt = LocalDateTime.now().toDatabaseString(),
//    updatedAt = LocalDateTime.now().toDatabaseString()
//)


fun ColorCategory.toUserResponse() = ColorCategoryUserClientResponse(
    id = id, colorName = colorName, colorValue = colorValue
)
fun List<ColorCategory>.toUserResponse() =map { it.toUserResponse() }
