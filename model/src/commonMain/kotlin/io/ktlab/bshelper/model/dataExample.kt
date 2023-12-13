package io.ktlab.bshelper.model

import io.ktlab.bshelper.model.enums.ECharacteristic
import io.ktlab.bshelper.model.enums.EMapDifficulty
import io.ktlab.bshelper.model.vo.BSMapVO
import io.ktlab.bshelper.model.vo.VersionWithDiffList
import kotlinx.datetime.LocalDateTime

private val fakeBSMap = BSMap(
    mapId = "Map123",
    name = "Sample Map",
    description = "This is a sample map description",
    uploaderId = 12345,
    bpm = 128.0,
    duration = 240,
    songName = "Sample Song",
    songSubname = "Subname",
    songAuthorName = "Sample Artist",
    levelAuthorName = "Sample Mapper",
    plays = 1000,
    downloads = 500,
    upVotes = 750,
    downVotes = 250,
    score = 4.5,
    automapper = false,
    ranked = true,
    qualified = true,
    bookmarked = false,
    uploaded = LocalDateTime(2021, 1, 1, 0, 0, 0),
    tags = listOf("Rhythm", "Music", "Gaming"),
    createdAt = LocalDateTime(2023, 4, 15, 12, 0, 0),
    updatedAt = LocalDateTime(2023, 4, 16, 8, 30, 0),
    lastPublishedAt = LocalDateTime(2023, 4, 16, 8, 30, 0)
)
private val bsUser = BSUser(
    id = 123,
    name = "John Doe",
    avatar = "https://example.com/avatar.jpg",
    description = "User description",
    type = "Regular",
    admin = false,
    curator = true,
    playlistUrl = "https://example.com/playlist",
    verifiedMapper = true
)
private val versions = listOf(
    VersionWithDiffList(
    version = BSMapVersion(
        hash = "abcdef123456",
        mapId = "Map789",
        state = "active",
        createdAt = LocalDateTime(2023, 9, 1, 10, 0, 0), // 设置日期时间
        sageScore = 5000,
        downloadURL = "https://example.com/map/download",
        coverURL = "https://example.com/map/cover",
        previewURL = "https://example.com/map/preview"
    ),
    diffs = listOf(
        MapDifficulty(
            seconds = 180.0,
            hash = "def456",
            mapId = "4fd0e",
            difficulty = EMapDifficulty.Expert,
            characteristic = ECharacteristic.Standard,
            notes = 1000,
            nps = 6.5,
            njs = 12.0,
            bombs = 20,
            obstacles = 5,
            offset = 1.5,
            events = 200,
            chroma = true,
            length = 240.0,
            me = true,
            ne = false,
            cinema = false,
            maxScore = 100000,
            label = "Custom Map"
        )
    )
)
)
val bsMapVO = BSMapVO(
    map = fakeBSMap,
    uploader = bsUser,
    versions = versions
)