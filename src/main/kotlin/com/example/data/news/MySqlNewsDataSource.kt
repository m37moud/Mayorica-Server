package com.example.data.news

import com.example.database.table.*
import com.example.models.News
import com.example.models.NewsCreate
import com.example.models.dto.NewsDto
import com.example.utils.AlreadyExistsException
import com.example.utils.ErrorException
import com.example.utils.NotFoundException
import com.example.utils.toDatabaseString
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
class MySqlNewsDataSource(private val db: Database) : NewsDataSource {
    override suspend fun getNumberOfNews(): Int {
        logger.debug { "getNumberOfNews call" }

        return withContext(Dispatchers.IO) {
            val newsList = db.from(NewsEntity)
                .select()
                .mapNotNull { rowToNews(it) }
            newsList.size
        }
    }

    override suspend fun getAllNews(): List<News> {
        return withContext(Dispatchers.IO) {
            val list = db.from(NewsEntity)
                .select()
                .orderBy(NewsEntity.createdAt.desc())
                .mapNotNull { rowToNews(it) }
            list
        }
    }

    override suspend fun getAllNewsPageable(
        query: String?,
        page: Int,
        perPage: Int,
        sortField: Column<*>,
        sortDirection: Int
    ): List<NewsDto> {
        logger.debug { "getAllNewsPageable call " }
        return withContext(Dispatchers.IO) {
            val myLimit = if (perPage > 100) 100 else perPage
            val myOffset = (page * perPage)
            val newsList = db.from(NewsEntity)
                .innerJoin(AdminUserEntity, on = NewsEntity.userAdminId eq AdminUserEntity.id)
                .select(
                    NewsEntity.id,
                    AdminUserEntity.username,
                    NewsEntity.title,
                    NewsEntity.newsDescription,
                    NewsEntity.image,
                    NewsEntity.createdAt,
                    NewsEntity.updatedAt,
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
                        it += (NewsEntity.title like "%${query}%")
                    }
                }
                .mapNotNull { rowToNewsDto(it) }
            newsList
        }
    }

    override suspend fun getNewsById(newsId: Int): News? {
        return withContext(Dispatchers.IO) {
            val news = db.from(NewsEntity)
                .select()
                .where {
                    NewsEntity.id eq newsId
                }
                .mapNotNull { rowToNews(it) }
                .firstOrNull()
            news
        }
    }

    override suspend fun getNewsByIdDto(newsId: Int): NewsDto? {
        return withContext(Dispatchers.IO) {
            val news = db.from(NewsEntity)
                .innerJoin(AdminUserEntity, on = NewsEntity.userAdminId eq AdminUserEntity.id)
                .select(
                    NewsEntity.id,
                    AdminUserEntity.username,
                    NewsEntity.title,
                    NewsEntity.newsDescription,
                    NewsEntity.image,
                    NewsEntity.createdAt,
                    NewsEntity.updatedAt,
                )
                .where {
                    NewsEntity.id eq newsId
                }
                .mapNotNull { rowToNewsDto(it) }
                .firstOrNull()
            news
        }

    }

    override suspend fun getNewsByTitleDto(title: String): NewsDto? {
        return withContext(Dispatchers.IO) {
            val news = db.from(NewsEntity)
                .innerJoin(AdminUserEntity, on = NewsEntity.userAdminId eq AdminUserEntity.id)
                .select(
                    NewsEntity.id,
                    AdminUserEntity.username,
                    NewsEntity.title,
                    NewsEntity.newsDescription,
                    NewsEntity.image,
                    NewsEntity.createdAt,
                    NewsEntity.updatedAt,
                )
                .where {
                    NewsEntity.title eq title
                }
                .mapNotNull { rowToNewsDto(it) }
                .firstOrNull()
            news
        }

    }

    override suspend fun addNews(news: NewsCreate): NewsDto? {
        if (getNewsByTitleDto(news.newsTitle) != null)
            throw AlreadyExistsException("this News inserted before .")
        if (createNews(news) < 0)
            throw ErrorException("Failed to create News .")
        return getNewsByTitleDto(news.newsTitle)
            ?: throw NotFoundException("failed to get News after created.")

    }

    override suspend fun createNews(news: NewsCreate): Int {
        return withContext(Dispatchers.IO) {
            val result = db.insert(NewsEntity) {
                set(it.title, news.newsTitle)
                set(it.image, news.newsImageUrl)
                set(it.newsDescription, news.newsDescription)
                set(it.userAdminId, news.userAdminId)
                set(it.createdAt, LocalDateTime.now())
                set(it.updatedAt, LocalDateTime.now())
            }
            result
        }

    }

    override suspend fun updateNews(id: Int, news: NewsCreate): Int {
        return withContext(Dispatchers.IO) {
            val result = db.update(NewsEntity) {
                set(it.title, news.newsTitle)
                set(it.image, news.newsImageUrl)
                set(it.newsDescription, news.newsDescription)
                set(it.userAdminId, news.userAdminId)
                set(it.updatedAt, LocalDateTime.now())
                where {
                    it.id eq id
                }

            }
            result
        }

    }

    override suspend fun deleteNews(newsId: Int): Int {
        return withContext(Dispatchers.IO) {
            val result = db.delete(NewsEntity) {

                it.id eq newsId
            }
            result
        }

    }

    override suspend fun deleteAllNews(): Int {
        return withContext(Dispatchers.IO) {
            val result = db.deleteAll(NewsEntity)
            result
        }

    }

    private fun rowToNews(row: QueryRowSet?): News? {
        return if (row == null) {
            null
        } else {
            val id = row[NewsEntity.id] ?: -1
            val title = row[NewsEntity.title] ?: ""
            val image = row[NewsEntity.image] ?: ""
            val newsDescription = row[NewsEntity.newsDescription] ?: ""
            val userAdminId = row[NewsEntity.userAdminId] ?: -1
            val createdAt = row[NewsEntity.createdAt] ?: LocalDateTime.now()
            val updatedAt = row[NewsEntity.updatedAt] ?: LocalDateTime.now()



            News(
                id = id,
                title = title,
                image = image,
                newsDescription = newsDescription,
                userAdminId = userAdminId,
                createdAt = createdAt.toDatabaseString(),
                updatedAt = updatedAt.toDatabaseString()


            )

        }
    }

    private fun rowToNewsDto(row: QueryRowSet?): NewsDto? {
        return if (row == null) {
            null
        } else {
            val id = row[NewsEntity.id] ?: -1
            val title = row[NewsEntity.title] ?: ""
            val image = row[NewsEntity.image]
            val newsDescription = row[NewsEntity.newsDescription] ?: ""
            val adminUserName = row[AdminUserEntity.username] ?: ""

            val createdAt = row[NewsEntity.createdAt] ?: ""
            val updatedAt = row[NewsEntity.updatedAt] ?: ""



            NewsDto(
                id = id,
                title = title,
                image = image,
                newsDescription = newsDescription,
                adminUserName = adminUserName,
                createdAt = createdAt.toString(),
                updatedAt = updatedAt.toString(),


                )

        }
    }

}