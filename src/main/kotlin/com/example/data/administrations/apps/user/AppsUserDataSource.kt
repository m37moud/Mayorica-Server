package com.example.data.administrations.apps.user

import com.example.models.AppCreate
import com.example.models.AppsModel
import com.example.models.dto.AppsModelDto
import com.example.models.dto.ColorCategoryDto
import org.ktorm.schema.Column

interface AppsUserDataSource {
    suspend fun getNumberOfApps(): Int
    suspend fun getAllAppsPageable(
        query: String?,
        page: Int,
        perPage: Int,
        sortField: Column<*>,
        sortDirection: Int
    ): List<AppsModelDto>

    suspend fun addApp(app: AppCreate): AppsModelDto
    suspend fun appDelete(appId: Int): Int
    suspend fun appUpdate(appId: Int, app: AppCreate): Int
    suspend fun getAppInfoById(appId: Int): AppsModel?
    suspend fun getAppInfoByIdDto(appId: Int): AppsModelDto?

    suspend fun getAppInfoByPackage(packageName: String): AppsModel?
    suspend fun getAppInfoByPackageDto(packageName: String): AppsModelDto?

    suspend fun getUserWithApp(apiKey: String): AppsModel?

}