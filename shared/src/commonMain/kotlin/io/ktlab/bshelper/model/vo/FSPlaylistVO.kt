package io.ktlab.bshelper.model.vo

import io.ktlab.bshelper.model.BSPlaylist
import io.ktlab.bshelper.model.BSUser
import io.ktlab.bshelper.model.FSPlaylist
import io.ktlab.bshelper.model.FSPlaylistView
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.model.enums.SyncStateEnum
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class FSPlaylistVO(
    val _name: String,
    val description: String?,
    val _mapAmount: Int,
    val totalDuration: Long?,
    val maxDuration: Long?,
    val avgDuration: Long?,
    val maxNote: Long?,
    val avgNote: Double?,
    val avgObstacle: Double?,
    val avgBomb: Double?,
    val maxNps: Double?,
    val avgNps: Double?,
    val bsPlaylistId: Int?,
    val basePath: String,
    val customTags: String?,
    val sync: SyncStateEnum,
    val syncTimestamp: Long,
    override val id: String,
    override val title: String,
    val bsPlaylist: BSPlaylistVO?,
    val manageDirId:Long,
    val topPlaylist: Boolean,
) : IPlaylist {
    fun toFSPlaylist(): FSPlaylist {
        return FSPlaylist(
            id = id,
            name = _name,
            description = description,
            customTags = customTags,
            sync = sync,
            syncTimestamp = syncTimestamp,
            basePath = basePath,
            bsPlaylistId = bsPlaylistId,
            manageDirId = manageDirId,
            topPlaylist = topPlaylist,
        )
    }

    override fun getAvatar(): String {
        bsPlaylist?.let {
            return it.owner.avatar
        } ?: return ""
    }

    override fun getName(): String {
        return _name
    }

    override fun getTotalDuration(): Duration {
        return totalDuration?.toDuration(DurationUnit.SECONDS) ?: Duration.ZERO
    }

    override fun getMapAmount(): Int {
        return _mapAmount
    }

    override fun getAuthor(): String {
        return bsPlaylist?.owner?.name ?: "unknown"
    }

    override fun getBSMaps(): List<IMap> {
        return listOf()
    }

    override fun getMaxDuration(): Duration {
        return maxDuration?.toDuration(DurationUnit.SECONDS) ?: Duration.ZERO
    }

    override fun getMaxNotes(): Int {
        return maxNote?.toInt() ?: 0
    }

    override fun getMaxNPS(): Double {
        return maxNps ?: 0.0
    }

    override fun getAvgDuration(): Duration {
        return avgDuration?.toDuration(DurationUnit.SECONDS) ?: Duration.ZERO
    }

    override fun getAvgNPS(): Double {
        return avgNps ?: 0.0
    }

    override fun getAvgNotes(): Double {
        return avgNote ?: 0.0
    }

    override fun getImage(): String {
        return bsPlaylist?.playlist?.playlistImage512 ?: ""
    }

    override fun isCustom(): Boolean {
        return bsPlaylist == null
    }

    override fun getMinNPS(): Double {
        return 0.0
    }

    override fun getPlaylistDescription(): String {
        return bsPlaylist?.playlist?.description ?: description ?: ""
    }

    override fun getTargetPath(): String {
        return basePath
    }

    companion object {
        fun convertDBOToVO(dbo: FSPlaylistView): FSPlaylistVO {
            return FSPlaylistVO(
                id = dbo.playlist_id,
                title = dbo.playlist_name,
                _name = dbo.playlist_name,
                description = dbo.playlist_description,
                bsPlaylistId = dbo.playlist_bsPlaylistId,
                sync = dbo.sync,
                syncTimestamp = dbo.playlist_syncTimestamp,
                customTags = dbo.playlist_customTags,
                basePath = dbo.playlist_basePath,
                _mapAmount = dbo.map_count?.toInt() ?: 0,
                totalDuration = dbo.sum_duration?.toLong(DurationUnit.SECONDS) ?: 0L,
                maxDuration = dbo.max_duration ?: 0L,
                avgDuration = dbo.avg_duration?.toLong() ?: 0L,
                maxNote = dbo.max_notes,
                avgNote = dbo.avg_notes,
                avgObstacle = dbo.avg_obstacles,
                avgBomb = dbo.avg_bombs,
                maxNps = dbo.max_nps,
                avgNps = dbo.avg_nps,
                bsPlaylist = buildBSPlaylist(dbo),
                topPlaylist = dbo.playlist_topPlaylist,
                manageDirId = dbo.manageDirId,
            )
        }

        private fun buildBSPlaylist(dbo: FSPlaylistView): BSPlaylistVO? {
            return dbo.bsPlaylist_id?.let {
                BSPlaylistVO(
                    playlist =
                        BSPlaylist(
                            id = it,
                            name = dbo.bsPlaylist_name!!,
                            description = dbo.bsPlaylist_description,
                            ownerId = dbo.bsPlaylist_ownerId!!,
                            curatorId = dbo.bsPlaylist_curatorId,
                            downloadURL = dbo.bsPlaylist_downloadURL!!,
                            playlistImage = dbo.bsPlaylist_playlistImage!!,
                            playlistImage512 = dbo.bsPlaylist_playlistImage512!!,
                            songsChangedAt = dbo.bsPlaylist_songsChangedAt,
                            updatedAt = dbo.bsPlaylist_updatedAt,
                            createdAt = dbo.bsPlaylist_createdAt!!,
                            type = dbo.bsPlaylist_type!!,
                            avgScore = dbo.bsPlaylist_avgScore!!,
                            upVotes = dbo.bsPlaylist_upVotes!!,
                            downVotes = dbo.bsPlaylist_downVotes!!,
                            mapperCount = dbo.bsPlaylist_mapperCount!!,
                            maxNps = dbo.bsPlaylist_maxNps!!,
                            minNps = dbo.bsPlaylist_minNps!!,
                            totalDuration = 0,
                        ),
                    owner =
                        BSUser(
                            id = dbo.owner_id!!,
                            name = dbo.owner_name!!,
                            avatar = dbo.owner_avatar!!,
                            type = dbo.owner_type!!,
                            description = dbo.owner_description!!,
                            admin = dbo.owner_admin!!,
                            curator = dbo.owner_curator!!,
                            playlistUrl = dbo.owner_playlistUrl!!,
                            verifiedMapper = dbo.owner_verifiedMapper,
                        ),
                    curator =
                        dbo.curator_id?.let {
                            BSUser(
                                id = dbo.curator_id,
                                name = dbo.curator_name!!,
                                avatar = dbo.curator_avatar!!,
                                type = dbo.curator_type!!,
                                description = dbo.curator_description!!,
                                admin = dbo.curator_admin!!,
                                curator = dbo.curator_curator!!,
                                playlistUrl = dbo.curator_playlistUrl!!,
                                verifiedMapper = dbo.curator_verifiedMapper,
                            )
                        },
                )
            }
        }
    }
}
