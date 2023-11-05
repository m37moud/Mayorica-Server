package com.example.data.about_us

import com.example.models.AboutUs

interface AboutUsDataSource {
    suspend fun getAboutUsInfo(): AboutUs?
    suspend fun createAboutUs(aboutUs: AboutUs): Int
    suspend fun updateAboutUs(aboutUs: AboutUs): Int
    suspend fun deleteAboutUs(aboutUsId: Int): Int
}