package com.example.mapper

import com.example.models.ColorCategory
import com.example.models.SizeCategory
import com.example.models.TypeCategory
import com.example.models.request.categories.ColorCategoryRequest
import com.example.models.request.categories.SizeCategoryRequest
import com.example.models.request.categories.TypeCategoryRequest
import com.example.utils.toDatabaseString
import java.time.LocalDateTime

fun TypeCategoryRequest.toModelCreate(userAdminId: Int) = TypeCategory(
    typeName = this.name,
    userAdminID = userAdminId,
    createdAt = LocalDateTime.now().toDatabaseString(),
    updatedAt = ""
)

fun SizeCategoryRequest.toModelCreate(userAdminId: Int) = SizeCategory(
    typeCategoryId = this.typeCategoryId,
    size = this.size,
    userAdminID = userAdminId,
    createdAt = LocalDateTime.now().toDatabaseString(),
    updatedAt = ""
)

fun ColorCategoryRequest.toModelCreate(userAdminId: Int) = ColorCategory(
    typeCategoryId = this.typeCategoryId,
    sizeCategoryId = this.sizeCategoryId,
    color = this.color,
    userAdminID = userAdminId,
    createdAt = LocalDateTime.now().toDatabaseString(),
    updatedAt = ""
)

fun TypeCategoryRequest.toModelUpdate(userAdminId: Int) = TypeCategory(
    typeName = this.name,
    userAdminID = userAdminId,
    createdAt = LocalDateTime.now().toDatabaseString(),
    updatedAt = LocalDateTime.now().toDatabaseString(),
)

fun SizeCategoryRequest.toModelUpdate(userAdminId: Int) = SizeCategory(
    typeCategoryId = this.typeCategoryId,
    size = this.size,
    userAdminID = userAdminId,
    createdAt = LocalDateTime.now().toDatabaseString(),
    updatedAt =LocalDateTime.now().toDatabaseString()
)

fun ColorCategoryRequest.toModelUpdate(userAdminId: Int) = ColorCategory(
    typeCategoryId = this.typeCategoryId,
    sizeCategoryId = this.sizeCategoryId,
    color = this.color,
    userAdminID = userAdminId,
    createdAt = LocalDateTime.now().toDatabaseString(),
    updatedAt =LocalDateTime.now().toDatabaseString()
)