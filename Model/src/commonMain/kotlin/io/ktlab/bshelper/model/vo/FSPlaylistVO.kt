package io.ktlab.bshelper.model.vo

import io.ktlab.bshelper.model.FSPlaylist
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.IPlaylist
import java.io.File
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class FSPlaylistVO(
    val uuid: String,
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
    val bsPlaylistId: String?,
    val basePath: String,
    val sync: Boolean,
    val syncTimestamp: Long,
    override val id: String,
    override val title: String,
): IPlaylist {
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
        return "unknown"
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

    override fun getAvgNPS(): String {
        return avgNps?.toString() ?: "0.0"
    }

    override fun getAvgNotes(): String {
        return avgNote?.toString() ?: "0.0"
    }

    override fun getImage(): String {
        return "unknown"
    }

    override fun isCustom(): Boolean {
        return true
    }

    override fun getMinNPS(): Double {
        return 0.0
    }

    override fun getTargetPath(): String {
        return basePath+ File.separator+_name
    }

    companion object {
        fun convertDBOToVO(dbo: FSPlaylist): FSPlaylistVO {
            return FSPlaylistVO(
                dbo.uuid,
                dbo.name,
                dbo.description,
                dbo.mapAmount,
                dbo.totalDuration,
                dbo.maxDuration,
                dbo.avgDuration,
                dbo.maxNote,
                dbo.avgNote,
                dbo.avgObstacle,
                dbo.avgBomb,
                dbo.maxNps,
                dbo.avgNps,
                dbo.bsPlaylistId,
                dbo.basePath,
                dbo.sync,
                dbo.syncTimestamp,
                dbo.uuid,
                dbo.name,
            )
        }
    }
}