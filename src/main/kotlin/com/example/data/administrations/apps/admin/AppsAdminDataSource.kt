package com.example.data.administrations.apps.admin

import com.example.models.AppsModel

interface AppsAdminDataSource {
    suspend fun appCreate(app : AppsModel) : Int
    suspend fun appDelete(appId : Int) : Int
    suspend fun appUpdate(app : AppsModel) : Int
    suspend fun getAppInfo(appId : Int) : AppsModel?
    suspend fun getUserWithApp(apiKey: String):AppsModel?

}