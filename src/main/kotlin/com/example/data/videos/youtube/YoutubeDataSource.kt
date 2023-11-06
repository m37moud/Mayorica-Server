package com.example.data.videos.youtube

import com.example.models.YoutubeLink

interface YoutubeDataSource {

    suspend fun getAllYoutubeVideoLinks(): List<YoutubeLink>
    suspend fun getSingleYoutubeVideoLinks(id:Int ): YoutubeLink?
    suspend fun addYoutubeLink (youtubeLink: YoutubeLink) : Int
    suspend fun updateYoutubeLink (youtubeLink: YoutubeLink) : Int
    suspend fun deleteYoutubeLink (youtubeId: Int) : Int
}