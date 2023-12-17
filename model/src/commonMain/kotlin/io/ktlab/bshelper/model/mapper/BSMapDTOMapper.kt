package io.ktlab.bshelper.model.mapper

import io.ktlab.bshelper.model.BSMap
import io.ktlab.bshelper.model.BSMapVersion
import io.ktlab.bshelper.model.BSUser
import io.ktlab.bshelper.model.MapDifficulty
import io.ktlab.bshelper.model.dto.BSMapDTO

fun BSMapDTO.convertToBSMapDBO(): BSMap {
    return BSMap(
        mapId = id,
        name = name,
        description = description,
        bookmarked = bookmarked,
        automapper = automapper,
        ranked = ranked,
        qualified = qualified,
        uploaderId = uploader.id,
        bpm = metadata.bpm,
        duration = metadata.duration,
        songName = metadata.songName,
        songSubname = metadata.songSubName,
        songAuthorName = metadata.songAuthorName,
        levelAuthorName = metadata.levelAuthorName,
        plays = stats.plays,
        downloads = stats.downloads,
        upVotes = stats.upvotes,
        downVotes = stats.downvotes,
        score = stats.score,
        uploaded = uploaded,
        tags = tags,
        createdAt = createdAt,
        updatedAt = updatedAt,
        lastPublishedAt = lastPublishedAt,
        curatorId = curator?.id,
    )
}

fun BSMapDTO.convertToBSUserDBO(): BSUser {
    return BSUser(
        id = uploader.id,
        name = uploader.name,
        avatar = uploader.avatar,
        description = uploader.description,
        type = uploader.type,
        admin = uploader.admin,
        curator = uploader.curator,
        playlistUrl = uploader.playlistUrl,
        verifiedMapper = uploader.verifiedMapper,
    )
}

fun BSMapDTO.convertToBSMapVersionDBO(): BSMapVersion {
    return BSMapVersion(
        hash = versions[0].hash,
        mapId = id,
        state = versions[0].state,
        createdAt = versions[0].createdAt,
        sageScore = versions[0].sageScore ?: 0,
        downloadURL = versions[0].downloadURL,
        coverURL = versions[0].coverURL,
        previewURL = versions[0].previewURL,
    )
}

fun BSMapDTO.convertToMapDifficulties(): List<MapDifficulty> {
    return versions[0].diffs.map {
        MapDifficulty(
            seconds = it.seconds,
            hash = versions[0].hash,
            mapId = id,
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
