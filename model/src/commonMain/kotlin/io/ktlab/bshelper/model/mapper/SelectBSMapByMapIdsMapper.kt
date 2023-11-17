package io.ktlab.bshelper.model.mapper

import io.ktlab.bshelper.model.*
import io.ktlab.bshelper.model.enums.ECharacteristic
import io.ktlab.bshelper.model.enums.EMapDifficulty
import io.ktlab.bshelper.model.vo.BSMapVO
import io.ktlab.bshelper.model.vo.VersionWithDiffList
import kotlinx.datetime.LocalDateTime

fun List<SelectAllByMapIds>.mapToVO():List<BSMapVO> = this
    .groupBy { it.mapId }
    .values
    .map {
        val first = it.first()
        val fsMap = BSMap(
            mapId = first.mapId,
            name = first.name,
            description = "",
            uploaderId = first.uploaderId,
            bpm = first.bsMapbpm,
            duration = first.bsMapDuration,
            songName = first.bsMapSongName,
            songSubname = first.bsMapSongSubName,
            songAuthorName = first.bsMapSongAuthorName,
            levelAuthorName = first.bsMapLevelAuthorName,
            plays = first.bsMapPlays,
            downloads = first.bsMapDownloads,
            upVotes = first.bsMapUpVotes,
            downVotes = first.bsMapDownVotes,
            score = first.bsMapScore,
            automapper = first.bsMapAutomapper,
            ranked = first.bsMapRanked,
            qualified = first.bsMapQualified,
            bookmarked = first.bsMapBookmarked,
            uploaded = first.bsMapUploaded,
            tags = first.bsMapTags,
            createdAt = first.bsMapCreatedAt,
            updatedAt = first.bsMapUpdatedAt,
            lastPublishedAt = first.bsMapLastPublishedAt,
        )
        val uploader = BSUser(
            id = first.uploaderId_!!,
            name = first.uploaderName!!,
            avatar = first.uploaderAvatar!!,
            admin = first.uploaderAdmin!!,
            type = first.uploaderType!!,
            curator = first.uploaderCurator!!,
            description = first.uploaderDescription!!,
            playlistUrl = first.uploaderPlaylistUrl!!,
            verifiedMapper = first.uploaderVerifiedMapper,
        )
        val versionWithDiffList= it.groupBy { it.bsMapVersionHash }.values.map {
            val version = it.first().let {
                BSMapVersion(
                    hash = it.bsMapVersionHash!!,
                    mapId = it.mapId,
                    key = "",
                    state = it.bsMapVersionState!!,
                    createdAt = it.bsMapVersionCreatedAt,
                    sageScore = 0,
                    downloadURL = it.bsMapVersionDownloadURL!!,
                    coverURL = it.bsMapVersionCoverURL!!,
                    previewURL = it.bsMapVersionPreviewURL!!,
                )
            }
            val difficulties = it.map {
                MapDifficulty(
                    hash = it.bsMapVersionHash!!,
                    mapId = it.mapId,
                    difficulty = it.diffDifficulty!!,
                    label = it.diffLabel,
                    seconds = it.diffSeconds!!,
                    characteristic = it.diffCharacteristic!!,
                    notes = it.diffNotes,
                    nps = it.diffNps,
                    njs = it.diffNjs,
                    bombs = it.diffBombs,
                    obstacles = it.diffObstacles,
                    offset = it.diffOffset,
                    events = it.diffEvents,
                    chroma = it.diffChroma,
                    length = it.diffLength,
                    me = it.diffMe,
                    ne = it.diffNe,
                    cinema = it.diffCinema,
                    maxScore = it.diffMaxScore,
                    uuid = ""
                )
            }
            VersionWithDiffList(version,difficulties)
        }
        BSMapVO(fsMap, uploader,versionWithDiffList)
    }