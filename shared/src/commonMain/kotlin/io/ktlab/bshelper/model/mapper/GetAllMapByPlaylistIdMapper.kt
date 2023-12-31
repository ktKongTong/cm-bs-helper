package io.ktlab.bshelper.model.mapper

import io.ktlab.bshelper.model.BSMap
import io.ktlab.bshelper.model.BSMapVersion
import io.ktlab.bshelper.model.BSUser
import io.ktlab.bshelper.model.FSMap
import io.ktlab.bshelper.model.GetAllByPlaylistId
import io.ktlab.bshelper.model.MapDifficulty
import io.ktlab.bshelper.model.vo.BsMapWithUploader
import io.ktlab.bshelper.model.vo.FSMapVO

fun List<GetAllByPlaylistId>.mapToVO(): List<FSMapVO> =
    this
        .groupBy { it.mapId }
        .values
        .map {
            try {
                val first = it.first()
                val fsMap =
                    FSMap(
                        mapId = first.mapId,
                        name = first.name,
                        hash = first.hash,
                        playlistId = first.playlistId,
                        playlistBasePath = first.playlistBasePath,
                        dirName = first.dirFilename,
                        duration = first.duration,
                        relativeSongFilename = first.relativeSongPath,
                        relativeCoverFilename = first.relativeCoverPath,
                        relativeInfoFilename = first.relativeInfoPath,
                        previewDuration = first.previewDuration,
                        previewStartTime = first.previewStartTime,
                        songSubname = first.songSubname,
                        songAuthorName = first.songAuthorName,
                        levelAuthorName = first.levelAuthorName,
                        bpm = first.bpm,
                        songName = first.songName,
                        active = first.active,
                        manageFolderId = first.manageFolderId,
                    )

                val difficulties =
                    it
                        .filter { diff -> diff.diffCharacteristic != null }
                        .map { diff ->
                            MapDifficulty(
                                hash = diff.hash,
                                characteristic = diff.diffCharacteristic!!,
                                difficulty = diff.diffDifficulty!!,
                                mapId = diff.mapId,
                                seconds = diff.diffSeconds!!,
                                notes = diff.diffNotes,
                                nps = diff.diffNps,
                                njs = diff.diffNjs,
                                bombs = diff.diffBombs,
                                obstacles = diff.diffObstacles,
                                offset = diff.diffOffset,
                                events = diff.diffEvents,
                                chroma = diff.diffChroma,
                                length = diff.diffLength,
                                me = diff.diffMe,
                                ne = diff.diffNe,
                                cinema = diff.diffCinema,
                                maxScore = diff.diffMaxScore,
                                label = diff.diffLabel,
                            )
                        }
                val bsUserWithUploader =
                    if (first.uploaderId != null) {
                        val user =
                            BSUser(
                                id = first.uploaderId,
                                name = first.uploaderName!!,
                                avatar = first.uploaderAvatar!!,
                                admin = first.uploaderAdmin!!,
                                type = first.uploaderType!!,
                                curator = first.uploaderCurator!!,
                                description = first.uploaderDescription!!,
                                playlistUrl = first.uploaderPlaylistUrl!!,
                                verifiedMapper = first.uploaderVerifiedMapper,
                            )
                        val curator =
                            if (first.curatorId != null) {
                                BSUser(
                                    id = first.curatorId,
                                    name = first.curatorName!!,
                                    avatar = first.curatorAvatar!!,
                                    admin = first.curatorAdmin!!,
                                    type = first.curatorType!!,
                                    curator = first.curatorCurator!!,
                                    description = first.curatorDescription!!,
                                    playlistUrl = first.curatorPlaylistUrl!!,
                                    verifiedMapper = first.curatorVerifiedMapper,
                                )
                            } else {
                                null
                            }
                        BsMapWithUploader(
                            uploader = user,
                            curator = curator,
                            bsMap =
                                BSMap(
                                    mapId = first.mapId,
                                    name = first.name,
                                    description = "",
                                    uploaderId = first.uploaderId,
                                    curatorId = first.curatorId,
                                    bpm = first.bsMapbpm!!,
                                    duration = first.bsMapDuration!!,
                                    songName = first.bsMapSongName!!,
                                    songSubname = first.bsMapSongSubName!!,
                                    songAuthorName = first.bsMapSongAuthorName!!,
                                    levelAuthorName = first.bsMapLevelAuthorName!!,
                                    plays = first.bsMapPlays!!,
                                    downloads = first.bsMapDownloads!!,
                                    upVotes = first.bsMapUpVotes!!,
                                    downVotes = first.bsMapDownVotes!!,
                                    score = first.bsMapScore!!,
                                    automapper = first.bsMapAutomapper!!,
                                    ranked = first.bsMapRanked!!,
                                    qualified = first.bsMapQualified!!,
                                    bookmarked = first.bsMapBookmarked!!,
                                    uploaded = first.bsMapUploaded!!,
                                    tags = first.bsMapTags!!,
                                    createdAt = first.bsMapCreatedAt!!,
                                    updatedAt = first.bsMapUpdatedAt!!,
                                    lastPublishedAt = first.bsMapLastPublishedAt!!,
                                ),
                            version =
                                BSMapVersion(
                                    hash = first.hash,
                                    mapId = first.mapId,
                                    state = first.bsMapVersionState ?: "",
                                    createdAt = first.bsMapVersionCreatedAt,
                                    sageScore = 0,
                                    downloadURL = first.bsMapVersionDownloadURL ?: "",
                                    coverURL = first.bsMapVersionCoverURL ?: "",
                                    previewURL = first.bsMapVersionPreviewURL ?: "",
                                ),
                            difficulties = difficulties,
                        )
                    } else {
                        null
                    }
                FSMapVO(fsMap, difficulties, bsUserWithUploader)
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
