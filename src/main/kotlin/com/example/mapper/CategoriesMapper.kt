package com.example.mapper

import com.example.models.TypeCategory
import com.example.models.request.categories.TypeCategoryRequest
import com.example.utils.toDatabaseString
import java.time.LocalDateTime

fun TypeCategoryRequest.toModelCreate(userAdminId:Int) = TypeCategory(
    name = this.name,
    userAdminID = userAdminId,
    createdAt = LocalDateTime.now().toDatabaseString(),
    updatedAt = ""
)

fun TypeCategoryRequest.toModelUpdate(userAdminId:Int) = TypeCategory(
    name = this.name,
    userAdminID = userAdminId,
    createdAt = "",
    updatedAt = LocalDateTime.now().toDatabaseString(),
)