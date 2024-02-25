package com.example.data.videos.youtube

import com.example.database.table.YouTubeLinkEntity
import com.example.models.VideoLinkCreate
import com.example.models.YoutubeLink
import com.example.utils.AlreadyExistsException
import com.example.utils.ErrorException
import com.example.utils.NotFoundException
import com.example.utils.toDatabaseString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Singleton
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.time.LocalDateTime

@Singleton
class MySqlYoutubeDataSource(private val db: Database) : YoutubeDataSource {
    override suspend fun getAllYoutubeVideoLinks(): List<YoutubeLink> {
        return withContext(Dispatchers.IO) {
            val list = db.from(YouTubeLinkEntity)
                .select()
                .orderBy(YouTubeLinkEntity.createdAt.desc())
                .mapNotNull { rowToYoutubeLink(it) }
            list
        }
    }

    override suspend fun getAllEnabledYoutubeVideoLinks(): List<YoutubeLink> {
        return withContext(Dispatchers.IO) {
            val list = db.from(YouTubeLinkEntity)
                .select()
                .where {
                    YouTubeLinkEntity.linkEnabled eq true
                }
                .orderBy(YouTubeLinkEntity.createdAt.desc())
                .mapNotNull { rowToYoutubeLink(it) }
            list
        }
    }

    override suspend fun getSingleYoutubeVideoLinks(id: Int): YoutubeLink? {
        return withContext(Dispatchers.IO) {
            val link = db.from(YouTubeLinkEntity)
                .select()
                .where {
                    YouTubeLinkEntity.id eq id
                }
                .mapNotNull { rowToYoutubeLink(it) }
                .firstOrNull()
            link
        }
    }

    override suspend fun getVideoLinkByIdLink(idLink: String): YoutubeLink? {
        return withContext(Dispatchers.IO) {
            val link = db.from(YouTubeLinkEntity)
                .select()
                .where {
                    YouTubeLinkEntity.idLink eq idLink
                }
                .mapNotNull { rowToYoutubeLink(it) }
                .firstOrNull()
            link
        }

    }

    override suspend fun addYoutubeLink(link: VideoLinkCreate): YoutubeLink {
        if (getVideoLinkByIdLink(link.idLink) != null) throw AlreadyExistsException("this item inserted before .")
        if (createYoutubeLink(link) < 0) throw ErrorException("Failed to create Video Link Item .")
        return getVideoLinkByIdLink(link.idLink)
            ?: throw NotFoundException("failed to get About Us Item after created.")

    }

    suspend fun createYoutubeLink(link: VideoLinkCreate): Int {
        return withContext(Dispatchers.IO) {
            val result = db.insert(YouTubeLinkEntity) {
                set(it.idLink, link.idLink)
                set(it.linkEnabled, link.linkEnabled)
                set(it.userAdminId, link.userAdminId)
                set(it.createdAt, LocalDateTime.now())
                set(it.updatedAt, LocalDateTime.now())
            }
            result
        }
    }

    override suspend fun updateYoutubeLink(youtubeLink: YoutubeLink): Int {
        return withContext(Dispatchers.IO) {
            val result = db.update(YouTubeLinkEntity) {
                set(it.idLink, youtubeLink.idLink)
                set(it.linkEnabled, youtubeLink.linkEnabled)
                set(it.userAdminId, youtubeLink.userAdminId)
                set(it.createdAt, LocalDateTime.now())
                set(it.updatedAt, LocalDateTime.now())
                where {
                    it.id eq youtubeLink.id
                }
            }
            result
        }
    }

    override suspend fun deleteYoutubeLink(youtubeId: Int): Int {
        return withContext(Dispatchers.IO) {
            val result = db.delete(YouTubeLinkEntity) {

                it.id eq youtubeId
            }
            result
        }
    }


    private fun rowToYoutubeLink(row: QueryRowSet?): YoutubeLink? {
        return if (row == null) {
            null
        } else {
            YoutubeLink(
                id = row[YouTubeLinkEntity.id] ?: -1,
                idLink = row[YouTubeLinkEntity.idLink] ?: "",
                linkEnabled = row[YouTubeLinkEntity.linkEnabled] ?: false,
                userAdminId = row[YouTubeLinkEntity.userAdminId] ?: -1,
                createdAt = row[YouTubeLinkEntity.createdAt]?.toString() ?: "",
                updatedAt = row[YouTubeLinkEntity.updatedAt]?.toString() ?: ""
            )
        }

    }
}