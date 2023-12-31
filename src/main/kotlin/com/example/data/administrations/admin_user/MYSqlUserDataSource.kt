package com.example.data.administrations.admin_user

import com.example.database.table.AdminUserEntity
import com.example.database.table.UserEntity
import com.example.models.AdminUser
import com.example.models.AdminUserDetail
import com.example.models.Role
import com.example.models.User
import com.example.models.mapper.toModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import org.koin.core.annotation.Singleton
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.schema.Column
import java.time.LocalDateTime

@Singleton
class MYSqlUserDataSource(
    private val db: Database
) : UserDataSource {

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

    override suspend fun register(newUser: AdminUser) = withContext(Dispatchers.IO) {
        val result = db.insert(AdminUserEntity) {
            set(it.full_name, newUser.full_name)
            set(it.username, newUser.username)
            set(it.password, newUser.password)
            set(it.salt, newUser.salt)
            set(it.role, newUser.role)
            set(it.created_at, LocalDateTime.now())
            set(it.updated_at, LocalDateTime.now())
        }
        result
    }

    override suspend fun create(newUser: User) = withContext(Dispatchers.IO) {
        val result = db.insert(UserEntity) {
            set(it.full_name, newUser.full_name)
            set(it.username, newUser.username)
            set(it.password, newUser.password)
            set(it.salt, newUser.salt)
            set(it.permission, newUser.role)
            set(it.created_at, LocalDateTime.now())
            set(it.updated_at, LocalDateTime.now())
        }
        result
    }


    override suspend fun getAllUser(): List<AdminUserDetail> = withContext(Dispatchers.IO) {
        val result = db.from(AdminUserEntity)
            .select()
            .orderBy(AdminUserEntity.created_at.desc())
            .mapNotNull {
                rowToAdminUser(it)
            }.toModel()


        result
    }

    override suspend fun getAllUserPageable(
        query: String?,
        permission: String?,
        page: Int,
        perPage: Int,
        sortField: Column<*>,
        sortDirection: Int
    ): List<AdminUserDetail> = withContext(Dispatchers.IO) {
        val myLimit = if (perPage > 100) 100 else perPage
        val myOffset = (page * perPage)
        val result = db.from(AdminUserEntity)
            .select()
            .limit(myLimit)
            .offset(myOffset)
            .orderBy(
                if (sortDirection > 0)
                    sortField.asc()
                else
                    sortField.desc()
            )
            .whereWithConditions {
                if (!query.isNullOrEmpty()) it += AdminUserEntity.full_name like "%${query}%" or (AdminUserEntity.username like "%${query}%")
                if (!permission.isNullOrEmpty()) it +=  (AdminUserEntity.role eq "$permission")
            }
            .mapNotNull {
                rowToAdminUser(it)
            }.toModel()

        result
    }

    override suspend fun isAdmin(id: Int): Boolean {
        return withContext(Dispatchers.IO) {
            val isAdmin = db.from(AdminUserEntity)
                .select(AdminUserEntity.role)
                .where {
                    AdminUserEntity.id eq id
                }
                .map { row -> row[AdminUserEntity.role] }
                .firstOrNull()
            isAdmin == Role.ADMIN.name
        }
    }

    override suspend fun updatePermission(id: Int, permission: String): Int {
        return withContext(Dispatchers.IO) {
            val result = db.update(AdminUserEntity) {
                set(it.role, permission)
                where {
                    it.id eq id
                }
            }
            result
        }
    }

    override suspend fun deleteAdminUser(id: Int): Int = withContext(Dispatchers.IO) {
        val result = db.delete(AdminUserEntity) {
            it.id eq id
        }
        result
    }

    private fun rowToAdminUser(row: QueryRowSet?): AdminUser? {
        return if (row == null) {
            null
        } else {
            val id = row[AdminUserEntity.id] ?: -1
            val fullName = row[AdminUserEntity.full_name] ?: ""
            val username = row[AdminUserEntity.username] ?: ""
            val password = row[AdminUserEntity.password] ?: ""
            val salt = row[AdminUserEntity.salt] ?: ""
            val role = row[AdminUserEntity.role] ?: Role.UNKNOWN.name
            val createdAt = row[AdminUserEntity.created_at] ?: ""
            val updatedAt = row[AdminUserEntity.updated_at] ?: ""
            AdminUser(
                id = id,
                full_name = fullName,
                username = username,
                password = password,
                salt = salt,
                role = role,
                created_at = createdAt.toString(),
                updated_at = updatedAt.toString()
            )
        }
    }

    private fun rowToUser(row: QueryRowSet?): User? {
        return if (row == null) {
            null
        } else {
            val id = row[UserEntity.id] ?: -1
            val fullName = row[UserEntity.full_name] ?: ""
            val username = row[UserEntity.username] ?: ""
            val password = row[UserEntity.password] ?: ""
            val salt = row[UserEntity.salt] ?: ""
            val role = row[UserEntity.permission] ?: -1
            val userAdminId = row[UserEntity.id] ?: -1
            val createdAt = row[UserEntity.created_at] ?: ""
            val updatedAt = row[UserEntity.updated_at] ?: ""
            User(
                id = id,
                full_name = fullName,
                username = username,
                password = password,
                salt = salt,
                role = role,
                userAdminID = userAdminId,
                created_at = createdAt.toString(),
                updated_at = updatedAt.toString()
            )
        }
    }


}