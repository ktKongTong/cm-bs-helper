package io.ktlab.bshelper.model.mapper

import io.ktlab.bshelper.model.BSMap
import io.ktlab.bshelper.model.BSMapVersion
import io.ktlab.bshelper.model.BSUser
import io.ktlab.bshelper.model.MapDifficulty
import io.ktlab.bshelper.model.dto.BSMapDTO

fun BSMapDTO.convertToBSMapDBO():BSMap{
    return BSMap(
        mapId = this.id,
        name = this.name,
        description = this.description,
        bookmarked = this.bookmarked,
        automapper = this.automapper,
        ranked = this.ranked,
        qualified = this.qualified,
        uploaderId = this.uploader.id,
        bpm = this.metadata.bpm,
        duration = this.metadata.duration,
        songName = this.metadata.songName,
        songSubname = this.metadata.songSubName,
        songAuthorName = this.metadata.songAuthorName,
        levelAuthorName = this.metadata.levelAuthorName,
        plays = this.stats.plays,
        downloads = this.stats.downloads,
        upVotes = this.stats.upvotes,
        downVotes = this.stats.downvotes,
        score = this.stats.score,
        uploaded = this.uploaded,
        tags = this.tags,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        lastPublishedAt = this.lastPublishedAt,
    )
}

fun BSMapDTO.convertToBSUserDBO():BSUser{
    return BSUser(
        id = this.uploader.id,
        name = this.uploader.name,
        avatar = this.uploader.avatar,
        description = this.uploader.description,
        type = this.uploader.type,
        admin = this.uploader.admin,
        curator = this.uploader.curator,
        playlistUrl = this.uploader.playlistUrl,
        verifiedMapper = this.uploader.verifiedMapper,
    )
}

fun BSMapDTO.convertToBSMapVersionDBO(): BSMapVersion {
    return BSMapVersion(
        hash = this.versions[0].hash,
        mapId = this.id,
        state = this.versions[0].state,
        createdAt = this.versions[0].createdAt,
        sageScore = this.versions[0].sageScore ?:0,
        downloadURL = this.versions[0].downloadURL,
        coverURL = this.versions[0].coverURL,
        previewURL = this.versions[0].previewURL,
    )
}

fun BSMapDTO.convertToMapDifficulties(): List<MapDifficulty> {
    return this.versions[0].diffs.map {
        MapDifficulty(
            seconds = it.seconds,
            hash = this.versions[0].hash,
            mapId = this.id,
            difficulty = it.difficulty,
            characteristic = it.characteristic,
            notes = it.notes,
            nps = it.nps,
            njs = it.njs,
            bombs = it.bombs,
            obstacles = it.obstacles,
            offset = it.offset,
            events = it.events,
            chroma = it.chroma,
            length = it.length,
            me = it.me,
            ne = it.ne,
            cinema = it.cinema,
            maxScore = it.maxScore,
            label = it.label,
        )

    }
}