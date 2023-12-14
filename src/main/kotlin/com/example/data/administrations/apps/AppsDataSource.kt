package com.example.data.administrations.apps

import com.example.models.AppsModel

interface AppsDataSource {
    suspend fun appCreate(app : AppsModel) : Int
    suspend fun appDelete(app : AppsModel) : Int
    suspend fun appUpdate(app : AppsModel) : Int
    suspend fun getAppInfo(app : AppsModel) : AppsModel?
    suspend fun getUserWithApp(apiKey: String):AppsModel?

}