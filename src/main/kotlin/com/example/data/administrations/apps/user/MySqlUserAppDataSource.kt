package com.example.data.administrations.apps.user

import com.example.data.administrations.apps.admin.AppsAdminDataSource
import com.example.database.table.AdminAppEntity
import com.example.database.table.ContactUsEntity
import com.example.models.AppsModel
import com.example.utils.generateApiKey
import com.example.utils.toDatabaseString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Singleton
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.time.LocalDateTime

@Singleton
class MySqlUserAppDataSource(private val db: Database) : AppsUserDataSource {
    /**
     * create app
     * @param app AppsModel app
     * @return Int  if inserted more than 1, 0 otherwise
     */
    override suspend fun appCreate(app: AppsModel): Int = withContext(Dispatchers.IO) {
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
            val result = db.delete(ContactUsEntity) {
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
    override suspend fun appUpdate(app: AppsModel): Int = withContext(Dispatchers.IO) {
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
        }
        result
    }

    override suspend fun getAppInfo(appId : Int): AppsModel? = withContext(Dispatchers.IO) {
        val result = db.from(AdminAppEntity)
            .select()
            .where {
                AdminAppEntity.id eq appId
            }
            .map { rowToAppsModel(it) }
            .firstOrNull()
        result

    }
    override suspend fun getAppInfo(packageName: String): AppsModel? = withContext(Dispatchers.IO) {
        val result = db.from(AdminAppEntity)
            .select()
            .where {
                AdminAppEntity.packageName eq packageName
            }
            .map { rowToAppsModel(it) }
            .firstOrNull()
        result

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

}