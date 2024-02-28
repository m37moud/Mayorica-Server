package com.example.mapper

import com.example.models.UpdateUserPasswordInfo
import com.example.models.UpdateUserProfileInfo
import com.example.models.dto.UpdateUserPasswordInfoDto
import com.example.models.dto.UpdateUserProfileInfoDto

fun UpdateUserProfileInfoDto.toEntity() = UpdateUserProfileInfo(
    fullName, userName
)
