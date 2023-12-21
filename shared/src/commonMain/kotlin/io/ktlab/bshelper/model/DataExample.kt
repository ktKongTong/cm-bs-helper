package io.ktlab.bshelper.model

import io.ktlab.bshelper.model.enums.ECharacteristic
import io.ktlab.bshelper.model.enums.EMapDifficulty
import io.ktlab.bshelper.model.vo.BSMapVO
import io.ktlab.bshelper.model.vo.BsMapWithUploader
import io.ktlab.bshelper.model.vo.FSMapVO
import io.ktlab.bshelper.model.vo.VersionWithDiffList
import io.ktlab.kown.model.DownloadListener
import io.ktlab.kown.model.DownloadTaskBO
import io.ktlab.kown.model.KownTaskStatus
import io.ktlab.kown.model.RenameStrategy
import kotlinx.datetime.LocalDateTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration

private val fakeBSMap =
    io.ktlab.bshelper.model.BSMap(
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
        score = 0.45,
        automapper = false,
        ranked = true,
        qualified = true,
        bookmarked = false,
        uploaded = LocalDateTime(2021, 1, 1, 0, 0, 0),
        tags = listOf("Rhythm", "Music", "Gaming"),
        createdAt = LocalDateTime(2023, 4, 15, 12, 0, 0),
        updatedAt = LocalDateTime(2023, 4, 16, 8, 30, 0),
        curatorId = null,
        lastPublishedAt = LocalDateTime(2023, 4, 16, 8, 30, 0),
    )
private val bsUser =
    io.ktlab.bshelper.model.BSUser(
        id = 123,
        name = "John Doe",
        avatar = "https://example.com/avatar.jpg",
        description = "User description",
        type = "Regular",
        admin = false,
        curator = true,
        playlistUrl = "https://example.com/playlist",
        verifiedMapper = true,
    )
private val diffs =
    listOf(
        io.ktlab.bshelper.model.MapDifficulty(
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
            label = "Custom Map",
        ),
    )
private val version =
    io.ktlab.bshelper.model.BSMapVersion(
        hash = "abcdef123456",
        mapId = "Map789",
        state = "active",
        createdAt = LocalDateTime(2023, 9, 1, 10, 0, 0),
        sageScore = 5000,
        downloadURL = "https://eu.cdn.beatsaver.com/a05378aa9e4bb399f354fd3ea3cdf56036518d18.zip",
        coverURL = "https://eu.cdn.beatsaver.com/a05378aa9e4bb399f354fd3ea3cdf56036518d18.jpg",
        previewURL = "https://eu.cdn.beatsaver.com/a05378aa9e4bb399f354fd3ea3cdf56036518d18.mp3",
    )
private val versions = listOf(VersionWithDiffList(version = io.ktlab.bshelper.model.version, diffs = io.ktlab.bshelper.model.diffs))

val bsMapVO =
    BSMapVO(
        map = io.ktlab.bshelper.model.fakeBSMap,
        uploader = io.ktlab.bshelper.model.bsUser,
        versions = io.ktlab.bshelper.model.versions,
    )

val fakeFSMap =
    io.ktlab.bshelper.model.FSMap(
        hash = "abcdef123456",
        name = "Sample Map",
        duration = 240L.toDuration(DurationUnit.SECONDS),
        previewStartTime = 0.0,
        previewDuration = 10L.toDuration(DurationUnit.SECONDS),
        bpm = 128.0,
        songName = "Sample Song",
        songSubname = "Subname",
        songAuthorName = "Sample Artist",
        levelAuthorName = "Sample Mapper",
        relativeCoverFilename = "cover.jpg",
        relativeSongFilename = "song.mp3",
        relativeInfoFilename = "info.json",
        dirName = "Map123",
        playlistBasePath = "https://example.com/playlist",
        playlistId = "Playlist123",
        active = true,
        mapId = "Map123",
        manageFolderId = 1,
    )

// val difficulties: List<MapDifficulty>? = null,
// val bsMapWithUploader: BsMapWithUploader? = null,
val fakeFSMapVO: FSMapVO =
    FSMapVO(
        fsMap = io.ktlab.bshelper.model.fakeFSMap,
        difficulties = io.ktlab.bshelper.model.diffs,
        bsMapWithUploader =
            BsMapWithUploader(
                bsMap = io.ktlab.bshelper.model.fakeBSMap,
                uploader = io.ktlab.bshelper.model.bsUser,
                version = io.ktlab.bshelper.model.version,
                difficulties = io.ktlab.bshelper.model.diffs,
            ),
    )

// fake a DownloadTaskBO
private val downloadTaskBO =
    DownloadTaskBO(
        taskId = "fake",
        title = "fake",
        tag = "fake",
        headers = mapOf(),
        status = KownTaskStatus.Running,
        url = "fake",
        eTag = "fake",
        dirPath = "fake",
        renameAble = false,
        renameStrategy = RenameStrategy.DEFAULT,
        filename = "fake",
        requestTimeout = 0,
        connectTimeout = 0,
        totalBytes = 33233,
        downloadedBytes = 333333,
        lastModifiedAt = 0,
        estimatedTime = 0,
        speed = 0,
        downloadListener = DownloadListener(),
        relateEntityId = "3f404",
    )
