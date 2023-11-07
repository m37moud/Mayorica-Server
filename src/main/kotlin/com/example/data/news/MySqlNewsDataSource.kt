package com.example.data.news

import com.example.database.table.AboutUsEntity
import com.example.database.table.NewsEntity
import com.example.database.table.YouTubeLinkEntity
import com.example.models.AboutUs
import com.example.models.News
import com.example.utils.toDatabaseString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.time.LocalDateTime

class MySqlNewsDataSource (private val db :Database) : NewsDataSource {
    override suspend fun getAllNews() :List<News> {
        return withContext(Dispatchers.IO) {
            val list = db.from(NewsEntity)
                .select()
                .orderBy(NewsEntity.createdAt.desc())
                .mapNotNull { rowToNews(it) }
            list
        }
    }

    override suspend fun getNewsById(newsId: Int) :News? {
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

    override suspend fun addNews(news: News): Int {
        return withContext(Dispatchers.IO) {
            val result = db.insert(NewsEntity) {
                set(it.title, NewsEntity.title)
                set(it.image, NewsEntity.image)
                set(it.newsDescription, NewsEntity.newsDescription)
                set(it.userAdminId, NewsEntity.userAdminId)
                set(it.createdAt, LocalDateTime.now())
                set(it.updatedAt, LocalDateTime.now())
            }
            result
        }

    }

    override suspend fun updateNews(news: News): Int {
        return withContext(Dispatchers.IO) {
            val result = db.update(NewsEntity) {
                set(it.title, NewsEntity.title)
                set(it.image, NewsEntity.image)
                set(it.newsDescription, NewsEntity.newsDescription)
                set(it.userAdminId, NewsEntity.userAdminId)
                set(it.updatedAt, LocalDateTime.now())
                where {
                    it.id eq news.id
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

}