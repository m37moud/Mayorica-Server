package com.example.data.administrations.apps.user

import com.example.models.AppsModel

interface AppsUserDataSource {
    suspend fun appCreate(app : AppsModel) : Int
    suspend fun appDelete(appId : Int) : Int
    suspend fun appUpdate(app : AppsModel) : Int
    suspend fun getAppInfo(appId : Int) : AppsModel?
    suspend fun getAppInfo(packageName : String) : AppsModel?

    suspend fun getUserWithApp(apiKey: String): AppsModel?

}