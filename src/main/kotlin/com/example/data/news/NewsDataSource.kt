package com.example.data.news

import com.example.models.News

interface NewsDataSource{
suspend fun getAllNews():List<News>
suspend fun getNewsById(newsId :Int) : News?
suspend fun addNews(news:News) :Int
suspend fun updateNews(news: News) :Int
suspend fun deleteNews(newsId :Int) :Int
suspend fun deleteAllNews() :Int

}