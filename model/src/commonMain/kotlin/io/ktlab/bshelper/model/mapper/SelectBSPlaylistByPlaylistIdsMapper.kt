package io.ktlab.bshelper.model.mapper

import io.ktlab.bshelper.model.BSPlaylist
import io.ktlab.bshelper.model.BSUser
import io.ktlab.bshelper.model.SelectByIds
import io.ktlab.bshelper.model.vo.BSPlaylistVO

fun List<SelectByIds>.mapToVO():List<BSPlaylistVO> = this
    .groupBy { it.id }
    .values.map {
        val first = it.first()
        val fsMap = BSPlaylist(
            id = first.id,
            name = first.name,
            description = first.description,
            ownerId = first.ownerId,
            curatorId = first.curatorId,
            downloadURL = first.downloadURL,
            playlistImage = first.playlistImage,
            playlistImage512 = first.playlistImage512,
            songsChangedAt = first.songsChangedAt,
            updatedAt = first.updatedAt,
            createdAt = first.createdAt,
            type = first.type,
            avgScore = first.avgScore,
            upVotes = first.upVotes,
            downVotes = first.downVotes,
            mapperCount = first.mapperCount,
            maxNps = first.maxNps,
            minNps = first.minNps,
            totalDuration = first.totalDuration,
        )
        val uploader = BSUser(
            id = first.ownerId,
            name = first.name_!!,
            avatar = first.avatar!!,
            admin = first.admin!!,
            type = first.type_!!,
            curator = first.curator!!,
            description = first.description_!!,
            playlistUrl = first.playlistUrl!!,
            verifiedMapper = first.verifiedMapper,
        )
        if (first.curatorId != null) {
            val curator = BSUser(
                id = first.curatorId,
                name = first.name__!!,
                avatar = first.avatar_!!,
                admin = first.admin_!!,
                type = first.type__!!,
                curator = first.curator_!!,
                description = first.description__!!,
                playlistUrl = first.playlistUrl_!!,
                verifiedMapper = first.verifiedMapper_,
            )
            BSPlaylistVO(fsMap, uploader, curator)
        } else {
            BSPlaylistVO(fsMap, uploader)
        }
    }