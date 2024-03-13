package com.example.mapper

import com.example.models.ContactUs
import com.example.models.ContactUsCreate
import com.example.models.SizeCategory
import com.example.models.dto.ContactUsCreateDto
import com.example.models.response.ContactUsUserClientResponse
import com.example.models.response.SizeCategoryUserClientResponse


fun ContactUsCreateDto.toModel(adminId: Int) = ContactUsCreate(
    country, governorate, address,
    telephone, email, mapLabel,
    latitude, longitude,
    webLink, fbLink, youtubeLink, instagramLink, linkedInLink,
    userAdminId = adminId
)


fun ContactUs.toUserResponse() = ContactUsUserClientResponse(
    id = id,
    country = country,
    governorate = governorate,
    address = address,
    telephone = telephone,
    email = email,
    mapLabel = mapLabel,
    latitude = latitude,
    longitude = longitude,
    webLink = webLink,
    fbLink = fbLink,
    youtubeLink = youtubeLink,
    instagramLink = instagramLink,
    linkedInLink = linkedInLink
)
