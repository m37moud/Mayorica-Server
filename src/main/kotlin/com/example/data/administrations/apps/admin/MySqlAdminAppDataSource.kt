package com.example.data.administrations.apps.admin

import com.example.data.administrations.apps.user.logger
import com.example.database.table.AdminAppEntity
import com.example.database.table.AdminUserEntity
import com.example.database.table.MobileAppEntity
import com.example.models.AppCreate
import com.example.models.AppsModel
import com.example.models.dto.AppsModelDto
import com.example.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Singleton
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.schema.Column
import java.time.LocalDateTime

@Singleton
class MySqlAdminAppDataSource(private val db: Database) : AppsAdminDataSource {

    override suspend fun getNumberOfApps(): Int {
        logger.debug { "getNumberOfApps call" }

        return withContext(Dispatchers.IO) {
            val appsList = db.from(AdminAppEntity)
                .select()
                .mapNotNull { rowToAppsModel(it) }
            appsList.size
        }
    }

    override suspend fun getAllAppsPageable(
        query: String?,
        page: Int,
        perPage: Int,
        sortField: Column<*>,
        sortDirection: Int
    ): List<AppsModelDto> {
        logger.debug { "getAllAppsPageable" }
        return withContext(Dispatchers.IO) {
            val myLimit = if (perPage > 100) 100 else perPage
            val myOffset = (page * perPage)
            val appsList = db.from(AdminAppEntity)
                .innerJoin(AdminUserEntity, on = AdminAppEntity.userAdminId eq AdminUserEntity.id)
                .select(
                    AdminAppEntity.id,
                    AdminUserEntity.username,
                    AdminAppEntity.packageName,
                    AdminAppEntity.apiKey,
                    AdminAppEntity.currentVersion,
                    AdminAppEntity.forceUpdate,
                    AdminAppEntity.updateMessage,
                    AdminAppEntity.enableApp,
                    AdminAppEntity.enableMessage,
                    AdminAppEntity.createdAt,
                    AdminAppEntity.updatedAt,
                )
                .limit(myLimit)
                .offset(myOffset)
                .orderBy(
                    if (sortDirection > 0)
                        sortField.asc()
                    else
                        sortField.desc()
                )
                .whereWithConditions {
                    if (!query.isNullOrEmpty()) {
                        it += (AdminAppEntity.packageName like "%${query}%")
                    }

                }
                .mapNotNull { rowToAppsModelDto(it) }
            appsList
        }
    }
    /**
     * create app
     * @param app AppsModel app
     * @return Int  if inserted more than 1, 0 otherwise
     */
    override suspend fun addApp(app: AppCreate): AppsModelDto {
        if (getAppInfoByPackage(app.packageName) != null)
            throw AlreadyExistsException("this app inserted before .")
        if (createApp(app) < 0)
            throw ErrorException("Failed to create app .")
        return getAppInfoByPackageDto(app.packageName)
            ?: throw NotFoundException("failed to get The app after created.")


    }

    private suspend fun createApp(app: AppCreate): Int = withContext(Dispatchers.IO) {
        val result = db.insert(AdminAppEntity) {
            set(it.packageName, app.packageName)
            set(it.apiKey, generateApiKey())
            set(it.currentVersion, app.currentVersion)
            set(it.forceUpdate, app.forceUpdate)
            set(it.updateMessage, app.updateMessage)
            set(it.enableApp, app.enableApp)
            set(it.enableMessage, app.enableMessage)
            set(it.userAdminId, app.userAdminId)
            set(it.createdAt, LocalDateTime.now())
            set(it.updatedAt, LocalDateTime.now())
        }
        result
    }


    /**
     * delete app
     * @param app AppsModel app
     * @return Int  if inserted more than 1, 0 otherwise
     */

    override suspend fun appDelete(appId: Int): Int {
        return withContext(Dispatchers.IO) {
            val result = db.delete(AdminAppEntity) {
                it.id eq appId
            }
            result
        }
    }

    /**
     * update app
     * @param app AppsModel app
     * @return Int  if inserted more than 1, 0 otherwise
     */
    override suspend fun appUpdate(appId: Int, app: AppCreate): Int = withContext(Dispatchers.IO) {
        val result = db.update(AdminAppEntity) {
            set(it.packageName, app.packageName)
//            set(it.apiKey, app.apiKey)
            set(it.currentVersion, app.currentVersion)
            set(it.forceUpdate, app.forceUpdate)
            set(it.updateMessage, app.updateMessage)
            set(it.enableApp, app.enableApp)
            set(it.enableMessage, app.enableMessage)
            set(it.userAdminId, app.userAdminId)
            set(it.updatedAt, LocalDateTime.now())
            where {
                it.id eq appId
            }
        }
        result
    }

    override suspend fun getAppInfoById(appId: Int): AppsModel? = withContext(Dispatchers.IO) {
        val result = db.from(AdminAppEntity)
            .select()
            .where {
                AdminAppEntity.id eq appId
            }
            .map { rowToAppsModel(it) }
            .firstOrNull()
        result

    }

    override suspend fun getAppInfoByPackage(packageName: String): AppsModel? = withContext(Dispatchers.IO) {
        val result = db.from(AdminAppEntity)
            .select()
            .where {
                AdminAppEntity.packageName eq packageName
            }
            .map { rowToAppsModel(it) }
            .firstOrNull()
        result

    }

    override suspend fun getAppInfoByPackageDto(packageName: String): AppsModelDto? = withContext(Dispatchers.IO) {
        val result = db.from(AdminAppEntity)
            .innerJoin(AdminUserEntity, on = AdminAppEntity.userAdminId eq AdminUserEntity.id)
            .select(
                AdminAppEntity.id,
                AdminUserEntity.username,
                AdminAppEntity.packageName,
                AdminAppEntity.apiKey,
                AdminAppEntity.currentVersion,
                AdminAppEntity.forceUpdate,
                AdminAppEntity.updateMessage,
                AdminAppEntity.enableApp,
                AdminAppEntity.enableMessage,
                AdminAppEntity.createdAt,
                AdminAppEntity.updatedAt,
            )
            .where {
                AdminAppEntity.packageName eq packageName
            }
            .map { rowToAppsModelDto(it) }
            .firstOrNull()
        result

    }

    override suspend fun getAppInfoByIdDto(appId: Int): AppsModelDto? {
        return withContext(Dispatchers.IO) {
            val result = db.from(AdminAppEntity)
                .innerJoin(AdminUserEntity, on = AdminAppEntity.userAdminId eq AdminUserEntity.id)
                .select(
                    AdminAppEntity.id,
                    AdminUserEntity.username,
                    AdminAppEntity.packageName,
                    AdminAppEntity.apiKey,
                    AdminAppEntity.currentVersion,
                    AdminAppEntity.forceUpdate,
                    AdminAppEntity.updateMessage,
                    AdminAppEntity.enableApp,
                    AdminAppEntity.enableMessage,
                    AdminAppEntity.createdAt,
                    AdminAppEntity.updatedAt,
                )
                .where {
                    AdminAppEntity.id eq appId
                }
                .map { rowToAppsModelDto(it) }
                .firstOrNull()
            result

        }

    }

    override suspend fun getUserWithApp(apiKey: String): AppsModel? = withContext(Dispatchers.IO) {
        val result = db.from(AdminAppEntity)
            .select()
            .where {
                AdminAppEntity.apiKey eq apiKey

            }
            .map { rowToAppsModel(it) }
            .firstOrNull()
        result

    }

    private fun rowToAppsModel(row: QueryRowSet?): AppsModel? {
        return if (row == null) {
            null
        } else {
            val id = row[AdminAppEntity.id] ?: -1
            val packageName = row[AdminAppEntity.packageName] ?: ""
            val apiKey = row[AdminAppEntity.apiKey] ?: ""
            val currentVersion = row[AdminAppEntity.currentVersion] ?: 0.0
            val forceUpdate = row[AdminAppEntity.forceUpdate] ?: false
            val updateMessage = row[AdminAppEntity.updateMessage] ?: ""
            val enableApp = row[AdminAppEntity.enableApp] ?: false
            val enableMessage = row[AdminAppEntity.enableMessage] ?: ""

            val userAdminId = row[AdminAppEntity.userAdminId] ?: -1
            val createdAt = row[AdminAppEntity.createdAt] ?: LocalDateTime.now()
            val updatedAt = row[AdminAppEntity.updatedAt] ?: LocalDateTime.now()



            AppsModel(
                id = id,
                packageName = packageName,
                apiKey = apiKey,
                currentVersion = currentVersion,
                forceUpdate = forceUpdate,
                updateMessage = updateMessage,
                enableApp = enableApp,
                enableMessage = enableMessage,
                userAdminId = userAdminId,
                createdAt = createdAt.toDatabaseString(),
                updatedAt = updatedAt.toDatabaseString()

            )

        }
    }

    private fun rowToAppsModelDto(row: QueryRowSet?): AppsModelDto? {
        return if (row == null) {
            null
        } else {
            val id = row[AdminAppEntity.id] ?: -1
            val packageName = row[AdminAppEntity.packageName] ?: ""
            val apiKey = row[AdminAppEntity.apiKey] ?: ""
            val currentVersion = row[AdminAppEntity.currentVersion] ?: 0.0
            val forceUpdate = row[AdminAppEntity.forceUpdate] ?: false
            val updateMessage = row[AdminAppEntity.updateMessage] ?: ""
            val enableApp = row[AdminAppEntity.enableApp] ?: false
            val enableMessage = row[AdminAppEntity.enableMessage] ?: ""
            val userAdminName = row[AdminUserEntity.username] ?: ""

            val createdAt = row[AdminAppEntity.createdAt] ?: ""
            val updatedAt = row[AdminAppEntity.updatedAt] ?: ""



            AppsModelDto(
                id = id,
                packageName = packageName,
                apiKey = apiKey,
                currentVersion = currentVersion,
                forceUpdate = forceUpdate,
                updateMessage = updateMessage,
                enableApp = enableApp,
                enableMessage = enableMessage,
                adminUserName = userAdminName,
                createdAt = createdAt.toString(),
                updatedAt = updatedAt.toString()

            )

        }
    }


}