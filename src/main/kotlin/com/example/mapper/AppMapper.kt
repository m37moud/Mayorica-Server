package com.example.mapper

import com.example.models.AppCreate
import com.example.models.dto.AppCreateDto

fun AppCreateDto.toEntity(adminId: Int) = AppCreate(
    packageName = packageName,
    currentVersion = currentVersion,
    userAdminId = adminId,
)