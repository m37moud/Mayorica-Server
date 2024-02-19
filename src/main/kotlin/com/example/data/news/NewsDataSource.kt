package com.example.data.news

import com.example.models.News
import com.example.models.NewsCreate
import com.example.models.dto.NewsDto
import com.example.models.dto.OfferDto
import org.ktorm.schema.Column

interface NewsDataSource {
    suspend fun getNumberOfNews(): Int

    suspend fun getAllNews(): List<News>
    suspend fun getAllNewsPageable(
        query: String?,
        page: Int,
        perPage: Int,
        sortField: Column<*>,
        sortDirection: Int
    ): List<NewsDto>

    suspend fun getNewsById(newsId: Int): News?
    suspend fun getNewsByIdDto(newsId: Int): NewsDto?
    suspend fun getNewsByTitleDto(title: String): NewsDto?

    suspend fun addNews(news: NewsCreate): NewsDto?
    suspend fun createNews(news: NewsCreate): Int
    suspend fun updateNews(id: Int, news: NewsCreate): Int
    suspend fun deleteNews(newsId: Int): Int
    suspend fun deleteAllNews(): Int

}