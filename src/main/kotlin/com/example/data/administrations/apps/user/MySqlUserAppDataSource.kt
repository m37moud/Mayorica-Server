package com.example.data.administrations.apps.user

import com.example.database.table.*
import com.example.models.AppCreate
import com.example.models.AppsModel
import com.example.models.dto.AppsModelDto
import com.example.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.core.annotation.Singleton
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.schema.Column
import java.time.LocalDateTime

val logger = KotlinLogging.logger { }

@Singleton
class MySqlUserAppDataSource(private val db: Database) : AppsUserDataSource {
    override suspend fun getNumberOfApps(): Int {
        logger.debug { "getNumberOfApps call" }

        return withContext(Dispatchers.IO) {
            val appsList = db.from(MobileAppEntity)
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
            val appsList = db.from(MobileAppEntity)
                .innerJoin(AdminUserEntity, on = MobileAppEntity.userAdminID eq AdminUserEntity.id)
                .select(
                    MobileAppEntity.id,
                    AdminUserEntity.username,
                    MobileAppEntity.packageName,
                    MobileAppEntity.apiKey,
                    MobileAppEntity.currentVersion,
                    MobileAppEntity.forceUpdate,
                    MobileAppEntity.updateMessage,
                    MobileAppEntity.enableApp,
                    MobileAppEntity.enableMessage,
                    MobileAppEntity.createdAt,
                    MobileAppEntity.updatedAt,
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
                        it += (MobileAppEntity.packageName like "%${query}%")
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
        val result = db.insert(MobileAppEntity) {
            set(it.packageName, app.packageName)
            set(it.apiKey, generateApiKey())
            set(it.currentVersion, app.currentVersion)
            set(it.forceUpdate, app.forceUpdate)
            set(it.updateMessage, app.updateMessage)
            set(it.enableApp, app.enableApp)
            set(it.enableMessage, app.enableMessage)
            set(it.userAdminID, app.userAdminId)
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
            val result = db.delete(MobileAppEntity) {
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
        val result = db.update(MobileAppEntity) {
            set(it.packageName, app.packageName)
//            set(it.apiKey, app.apiKey)
            set(it.currentVersion, app.currentVersion)
            set(it.forceUpdate, app.forceUpdate)
            set(it.updateMessage, app.updateMessage)
            set(it.enableApp, app.enableApp)
            set(it.enableMessage, app.enableMessage)
            set(it.userAdminID, app.userAdminId)
            set(it.updatedAt, LocalDateTime.now())
            where {
                it.id eq appId
            }
        }
        result
    }

    override suspend fun getAppInfoById(appId: Int): AppsModel? = withContext(Dispatchers.IO) {
        val result = db.from(MobileAppEntity)
            .select()
            .where {
                MobileAppEntity.id eq appId
            }
            .map { rowToAppsModel(it) }
            .firstOrNull()
        result

    }

    override suspend fun getAppInfoByIdDto(appId: Int): AppsModelDto? {
        logger.debug { "getAppInfoByIdDto" }
        return withContext(Dispatchers.IO) {
            val result = db.from(MobileAppEntity)
                .innerJoin(AdminUserEntity, on = MobileAppEntity.userAdminID eq AdminUserEntity.id)
                .select(
                    MobileAppEntity.id,
                    AdminUserEntity.username,
                    MobileAppEntity.packageName,
                    MobileAppEntity.apiKey,
                    MobileAppEntity.currentVersion,
                    MobileAppEntity.forceUpdate,
                    MobileAppEntity.updateMessage,
                    MobileAppEntity.enableApp,
                    MobileAppEntity.enableMessage,
                    MobileAppEntity.createdAt,
                    MobileAppEntity.updatedAt,
                )
                .where {
                    MobileAppEntity.id eq appId
                }
                .map { rowToAppsModelDto(it) }
                .firstOrNull()
            result

        }

    }

    override suspend fun getAppInfoByPackage(packageName: String): AppsModel? = withContext(Dispatchers.IO) {
        val result = db.from(MobileAppEntity)
            .select()
            .where {
                MobileAppEntity.packageName eq packageName
            }
            .map { rowToAppsModel(it) }
            .firstOrNull()
        result

    }

    override suspend fun getAppInfoByPackageDto(packageName: String): AppsModelDto? = withContext(Dispatchers.IO) {
        val result = db.from(MobileAppEntity)
            .innerJoin(AdminUserEntity, on = MobileAppEntity.userAdminID eq AdminUserEntity.id)
            .select(
                MobileAppEntity.id,
                AdminUserEntity.username,
                MobileAppEntity.packageName,
                MobileAppEntity.apiKey,
                MobileAppEntity.currentVersion,
                MobileAppEntity.forceUpdate,
                MobileAppEntity.updateMessage,
                MobileAppEntity.enableApp,
                MobileAppEntity.enableMessage,
                MobileAppEntity.createdAt,
                MobileAppEntity.updatedAt,
            )
            .where {
                MobileAppEntity.packageName eq packageName
            }
            .map { rowToAppsModelDto(it) }
            .firstOrNull()
        result

    }


    override suspend fun getUserWithApp(apiKey: String): AppsModel? = withContext(Dispatchers.IO) {
        val result = db.from(MobileAppEntity)
            .select()
            .where {
                MobileAppEntity.apiKey eq apiKey

            }
            .map { rowToAppsModel(it) }
            .firstOrNull()
        result

    }


    private fun rowToAppsModel(row: QueryRowSet?): AppsModel? {
        return if (row == null) {
            null
        } else {
            val id = row[MobileAppEntity.id] ?: -1
            val packageName = row[MobileAppEntity.packageName] ?: ""
            val apiKey = row[MobileAppEntity.apiKey] ?: ""
            val currentVersion = row[MobileAppEntity.currentVersion] ?: 0.0
            val forceUpdate = row[MobileAppEntity.forceUpdate] ?: false
            val updateMessage = row[MobileAppEntity.updateMessage] ?: ""
            val enableApp = row[MobileAppEntity.enableApp] ?: false
            val enableMessage = row[MobileAppEntity.enableMessage] ?: ""

            val userAdminId = row[MobileAppEntity.userAdminID] ?: -1
            val createdAt = row[MobileAppEntity.createdAt] ?: LocalDateTime.now()
            val updatedAt = row[MobileAppEntity.updatedAt] ?: LocalDateTime.now()



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
            val id = row[MobileAppEntity.id] ?: -1
            val packageName = row[MobileAppEntity.packageName] ?: ""
            val apiKey = row[MobileAppEntity.apiKey] ?: ""
            val currentVersion = row[MobileAppEntity.currentVersion] ?: 0.0
            val forceUpdate = row[MobileAppEntity.forceUpdate] ?: false
            val updateMessage = row[MobileAppEntity.updateMessage] ?: ""
            val enableApp = row[MobileAppEntity.enableApp] ?: false
            val enableMessage = row[MobileAppEntity.enableMessage] ?: ""
            val userAdminName = row[AdminUserEntity.username] ?: ""

            val createdAt = row[MobileAppEntity.createdAt] ?: ""
            val updatedAt = row[MobileAppEntity.updatedAt] ?: ""



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