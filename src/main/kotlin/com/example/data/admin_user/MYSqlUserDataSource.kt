package com.example.data.admin_user

import com.example.database.table.AdminUserEntity
import com.example.models.AdminUser
import com.example.models.Role
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.*

class MYSqlUserDataSource(private val db: Database) : UserDataSource {

    /**
     * Find user by username
     * @param username User username
     * @return AdminUser? User if exists, null otherwise
     */
    override suspend fun getUserByUsername(username: String): AdminUser? {
        return withContext(Dispatchers.IO) {
            val user = db.from(AdminUserEntity)
                .select()
                .where {
                    AdminUserEntity.username eq username
                }.map {
                    rowToAdminUser(it)
                }.firstOrNull()
            user
        }
    }

    override suspend fun register(newUser: AdminUser) = withContext(Dispatchers.IO){
            val result=db.insert(AdminUserEntity) {
                set(it.username, newUser.username)
                set(it.password, newUser.password)
                set(it.salt, newUser.salt)
                set(it.role, newUser.role)
                set(it.created_at, newUser.created_at)
                set(it.updated_at,newUser.updated_at)
            }
            result
        }


    private fun rowToAdminUser(row: QueryRowSet?): AdminUser? {
        return if (row == null) {
            null
        } else {
            val id = row[AdminUserEntity.id] ?: -1
            val full_name = row[AdminUserEntity.full_name] ?: ""
            val username = row[AdminUserEntity.username] ?: ""
            val password = row[AdminUserEntity.password] ?: ""
            val salt = row[AdminUserEntity.salt] ?: ""
            val role = row[AdminUserEntity.role] ?: Role.UNKNOWN.name
            val createdAt = row[AdminUserEntity.created_at] ?: ""
            val updatedAt = row[AdminUserEntity.updated_at] ?: ""
            AdminUser(
                id = id,
                full_name = full_name,
                username = username,
                password = password,
                salt = salt,
                role = role,
                created_at = createdAt,
                updated_at = updatedAt
            )
        }
    }

    override suspend fun getAllUser(): List<AdminUser> = withContext(Dispatchers.IO) {
        val notes = db.from(AdminUserEntity).select()
            .mapNotNull {
                rowToNote(it)
            }

        notes
    }

    private fun rowToNote(row: QueryRowSet?): AdminUser? {
        return if (row == null) {
            null
        } else {
            AdminUser(
                row[AdminUserEntity.id] ?: -1,
                row[AdminUserEntity.full_name] ?: "",
                row[AdminUserEntity.username] ?: "",
                row[AdminUserEntity.password] ?: "",
                row[AdminUserEntity.salt] ?:"",
                row[AdminUserEntity.role] ?: "",
                row[AdminUserEntity.created_at] ?:"#333333",
                row[AdminUserEntity.updated_at] ?:"",
            )
        }
    }
}