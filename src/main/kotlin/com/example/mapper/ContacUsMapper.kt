package com.example.mapper

import com.example.models.ContactUs
import com.example.models.ContactUsCreate
import com.example.models.dto.ContactUsCreateDto


fun ContactUsCreateDto.toModel(adminId: Int) = ContactUsCreate(
    country, governorate, address,
    telephone, email, mapLabel,
    latitude, longitude,
    webLink, fbLink, youtubeLink, instagramLink, linkedInLink,
    userAdminId = adminId
)
