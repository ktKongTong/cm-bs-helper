package io.ktlab.bshelper.model.vo

import io.ktlab.bshelper.model.FSPlaylist
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.IPlaylist
import java.io.File
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class FSPlaylistVO(
    public val _uuid: String,
    public val _name: String,
    public val _description: String?,
    public val _mapAmount: Int,
    public val _totalDuration: Long?,
    public val _maxDuration: Long?,
    public val _avgDuration: Long?,
    public val _maxNote: Long?,
    public val _avgNote: Double?,
    public val _avgObstacle: Double?,
    public val _avgBomb: Double?,
    public val _maxNps: Double?,
    public val _avgNps: Double?,
    public val _bsPlaylistId: String?,
    public val _basePath: String,
    public val _sync: Boolean,
    public val _syncTimestamp: Long,
    override val id: String,
    override val title: String,
): IPlaylist {
    override fun getName(): String {
        return _name
    }

    override fun getTotalDuration(): Duration {
        return _totalDuration?.toDuration(DurationUnit.SECONDS) ?: Duration.ZERO
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
        return _maxDuration?.toDuration(DurationUnit.SECONDS) ?: Duration.ZERO
    }

    override fun getMaxNotes(): Int {
        return _maxNote?.toInt() ?: 0
    }

    override fun getMaxNPS(): Double {
        return _maxNps ?: 0.0
    }

    override fun getAvgDuration(): Duration {
        return _avgDuration?.toDuration(DurationUnit.SECONDS) ?: Duration.ZERO
    }

    override fun getAvgNPS(): String {
        return _avgNps?.toString() ?: "0.0"
    }

    override fun getAvgNotes(): String {
        return _avgNote?.toString() ?: "0.0"
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
        return _basePath+ File.separator+_name
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