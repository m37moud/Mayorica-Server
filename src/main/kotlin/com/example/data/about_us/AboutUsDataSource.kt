package com.example.data.about_us

import com.example.models.AboutUs

interface AboutUsDataSource {
    suspend fun getAllAboutUsInfo(): List<AboutUs>
    suspend fun getAboutUsInfoById(id: Int): AboutUs?
    suspend fun getAboutUsInfoByTitle(title: String): AboutUs?
    suspend fun addAboutUs(aboutUs: AboutUs): AboutUs
    suspend fun updateAboutUs(aboutUs: AboutUs): Int
    suspend fun deleteAboutUs(id: Int): Int
    suspend fun deleteAllAboutUs(): Int
}