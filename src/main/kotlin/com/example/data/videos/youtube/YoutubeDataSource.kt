package com.example.data.videos.youtube

import com.example.models.VideoLinkCreate
import com.example.models.YoutubeLink

interface YoutubeDataSource {

    suspend fun getAllYoutubeVideoLinks(): List<YoutubeLink>
    suspend fun getAllEnabledYoutubeVideoLinks(): List<YoutubeLink>
    suspend fun getSingleYoutubeVideoLinks(id:Int ): YoutubeLink?

    suspend fun getVideoLinkByIdLink(idLink:String ): YoutubeLink?
    suspend fun addYoutubeLink (link: VideoLinkCreate) : YoutubeLink
    suspend fun updateYoutubeLink (youtubeLink: YoutubeLink) : Int
    suspend fun deleteYoutubeLink (youtubeId: Int) : Int
}