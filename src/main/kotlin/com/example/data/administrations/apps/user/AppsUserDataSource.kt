package com.example.data.administrations.apps.user

import com.example.models.AppsModel
import com.example.models.dto.AppsModelDto
import com.example.models.dto.ColorCategoryDto
import org.ktorm.schema.Column

interface AppsUserDataSource {
    suspend fun getAllAppsPageable(
        query: String?,
        page: Int,
        perPage: Int,
        sortField: Column<*>,
        sortDirection: Int
    ): List<AppsModelDto>
    suspend fun appCreate(app : AppsModel) : Int
    suspend fun appDelete(appId : Int) : Int
    suspend fun appUpdate(app : AppsModel) : Int
    suspend fun getAppInfo(appId : Int) : AppsModel?
    suspend fun getAppInfo(packageName : String) : AppsModel?

    suspend fun getUserWithApp(apiKey: String): AppsModel?

}