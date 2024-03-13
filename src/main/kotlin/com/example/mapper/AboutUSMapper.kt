package com.example.mapper

import com.example.models.AboutUs
import com.example.models.response.AboutUsUserClientResponse

fun AboutUs.toUSerResponse() = AboutUsUserClientResponse(
    id = id, title = title, information = information
)
fun List<AboutUs>.toUSerResponse() = map { it.toUSerResponse() }