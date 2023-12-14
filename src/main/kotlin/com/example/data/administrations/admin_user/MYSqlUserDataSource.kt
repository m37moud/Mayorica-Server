package com.example.data.administrations.admin_user

import com.example.database.table.AdminUserEntity
import com.example.models.AdminUser
import com.example.models.Role
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import org.koin.core.annotation.Singleton
import org.ktorm.database.Database
import org.ktorm.dsl.*
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

    override suspend fun register(newUser: AdminUser) = withContext(Dispatchers.IO){
            val result=db.insert(AdminUserEntity) {
                set(it.full_name, newUser.full_name)
                set(it.username, newUser.username)
                set(it.password, newUser.password)
                set(it.salt, newUser.salt)
                set(it.role, newUser.role)
                set(it.created_at, LocalDateTime.now())
                set(it.updated_at,LocalDateTime.now())
            }
            result
        }



    override suspend fun getAllUser(): List<AdminUser> = withContext(Dispatchers.IO) {
        val notes = db.from(AdminUserEntity)
            .select()
            .orderBy(AdminUserEntity.created_at.desc())
            .mapNotNull {
                rowToAdminUser(it)
            }

        notes
    }

    override suspend fun isAdmin(id:Int): Boolean {
      return withContext(Dispatchers.IO){
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


}