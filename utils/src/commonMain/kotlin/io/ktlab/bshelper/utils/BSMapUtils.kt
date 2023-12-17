package io.ktlab.bshelper.utils

// import io.ktlab.bsmg.FSMapInfo
import io.beatmaps.common.beatsaber.BSDifficulty
import io.beatmaps.common.beatsaber.BSDifficultyV3
import io.ktlab.bshelper.model.FSMap
import io.ktlab.bshelper.model.FSPlaylist
import io.ktlab.bshelper.model.enums.SyncStateEnum
import io.ktlab.bshelper.model.scanner.IExtractedMapInfo
import io.ktlab.bshelper.model.scanner.ScannerException
import io.ktlab.bsmg.FSMapInfo
import kotlinx.datetime.Clock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okio.Buffer
import okio.FileSystem
import okio.HashingSink
import okio.Path
import okio.Path.Companion.toPath
import okio.blackholeSink
import okio.buffer
import okio.source
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class ExtractedMapInfo(
    val mapId: String? = null,
    val hash: String,
    val mapInfo: FSMapInfo,
    val songDuration: Double = 0.0,
    val basePath: Path,
    val infoFilename: String,
    val songFilename: String,
    val coverFilename: String,
    val v2MapObjectMap: Map<String, BSDifficulty>? = null,
    val v3MapObjectMap: Map<String, BSDifficultyV3>? = null,
) {
    fun generateFSMapDBO(playlistId: String): FSMap {
        return FSMap(
            mapId = mapId ?: "",
            name = mapInfo.songName,
            duration = songDuration.toDuration(DurationUnit.SECONDS),
            relativeInfoFilename = coverFilename,
            relativeCoverFilename = songFilename,
            relativeSongFilename = infoFilename,
            dirName = basePath.name,
            playlistBasePath = basePath.parent?.toString() ?: "",
            hash = hash,
            active = true,
            playlistId = playlistId,
            bpm = mapInfo.bpm,
            songName = mapInfo.songName,
            songSubname = mapInfo.songAuthorName,
            songAuthorName = mapInfo.songAuthorName,
            levelAuthorName = mapInfo.songAuthorName,
            previewDuration = mapInfo.previewDuration.toDuration(DurationUnit.SECONDS),
            previewStartTime = mapInfo.previewStartTime,
        )
    }
}

fun newFSPlaylist(
    basePath: String,
    name: String = "",
    topPlaylist: Boolean = false,
): FSPlaylist {
    return FSPlaylist(
        id = basePath,
        name = name,
        description = "",
        sync = SyncStateEnum.SYNCED,
        bsPlaylistId = null,
        basePath = basePath,
        syncTimestamp = Clock.System.now().epochSeconds,
        customTags = "",
        topPlaylist = topPlaylist,
    )
}

fun newFSPlaylist(
    name: String = "",
    customTags: String? = null,
    description: String? = null,
): FSPlaylist {
    return FSPlaylist(
        id = "",
        name = name,
        description = description,
        sync = SyncStateEnum.SYNCED,
        bsPlaylistId = null,
        basePath = "",
        syncTimestamp = Clock.System.now().epochSeconds,
        customTags = customTags,
        topPlaylist = false,
    )
}

class BSMapUtils {
    companion object {
        private val json =
            Json {
                ignoreUnknownKeys = true
                isLenient = true
            }

        fun checkIfBSMap(path: Path): Boolean {
            return try {
                val metadata = FileSystem.SYSTEM.metadataOrNull(path)
                if (metadata?.isDirectory != true) {
                    return false
                }
                val files = FileSystem.SYSTEM.list(path)
                if (files.size > 20) {
                    return false
                }
                return files.any { it.name.lowercase() == "info.dat" }
            } catch (e: Exception) {
                false
            }
        }

        fun checkIfBSMap(file: File): Boolean {
            if (!file.isDirectory) {
                return false
            }
            return file.listFiles()?.any { it != null && it.name.lowercase() == "info.dat" } ?: false
        }

        private fun mapDigest(path: Path): Pair<String, ScannerException?> {
//            val path = infoFilePath
            val files = FileSystem.SYSTEM.list(path)
            val infoFile =
                files.find { it.name.lowercase() == "info.dat" }
                    ?: return "" to ScannerException.FileMissingException("Info.dat or info.dat not found", mapDir = path.toString())
            HashingSink.sha1(blackholeSink()).use { sink ->
                val paths =
                    infoFile.toFile().source().buffer().use inner@{ source ->
                        val copySink = Buffer()
                        source.readAll(copySink)
                        val str = copySink.copy().readUtf8()
                        copySink.readAll(sink)
                        val infoc = json.decodeFromString<FSMapInfo>(str)
                        val paths =
                            infoc.difficultyBeatmapSets.flatMap { bms ->
                                bms.difficultyBeatmaps.map { df ->
                                    path.resolve(df.beatmapFilename) to bms.characteristicName + df.difficulty
                                }
                            }
                        return@inner paths
                    }
                var exception: ScannerException? = null
                paths.map { pathInfo ->
                    if (!FileSystem.SYSTEM.exists(pathInfo.first)) {
                        exception = ScannerException.FileMissingException("File missing", mapDir = pathInfo.first.toString())
                    } else {
                        pathInfo.first.toFile().source().buffer().readAll(sink)
                    }
                }
                return sink.hash.hex() to exception
            }
        }

        @OptIn(ExperimentalSerializationApi::class)
        fun extractMapInfoFromDir(
            playlistAbsolutePath: String,
            file: File,
            playlistId: String,
        ): ExtractedMapInfo {
            val basePath = file.absolutePath
            val mapId: String
            try {
                mapId = file.name.substring(0, file.name.indexOf(" (")).trim()
            } catch (e: Exception) {
                throw Exception("${file.absolutePath}: cannot extract mapId")
            }

            val files = file.listFiles() ?: throw Exception("${file.absolutePath}: File list is null")
            val infoFile =
                files.find { it.name.lowercase() == "info.dat" }
                    ?: throw Exception("${file.absolutePath}: Info.dat or info.dat not found")

            val md = MessageDigest.getInstance("SHA1")
            md.reset()
            val infoContent = infoFile.readText()
            val info = json.decodeFromString<FSMapInfo>(infoContent)
            val v2DifficultyMap = mutableMapOf<String, BSDifficulty>()
            val v3DifficultyMap = mutableMapOf<String, BSDifficultyV3>()
            val infobytes = infoFile.readBytes()
            val digest =
                HashingSink.sha1(blackholeSink()).use { sink ->
                    val paths =
                        file.source().buffer().use inner@{ source ->
                            source.readAll(sink)
                            source.readUtf8()
                            val infoc = json.decodeFromString<FSMapInfo>(source.readUtf8())
                            val paths =
                                infoc.difficultyBeatmapSets.flatMap { bms ->
                                    bms.difficultyBeatmaps.map { df ->
                                        basePath.toPath().resolve(df.beatmapFilename) to bms.characteristicName + df.difficulty
                                    }
                                }
                            return@inner paths
                        }
                    paths.map { pathInfo ->
                        val f = pathInfo.first.toFile()
                        f.source().buffer().readAll(sink)
                    }
                    return@use sink.hash.hex()
                }

            val buffer = infoFile.source().buffer()
            md.update(infobytes)
            val paths =
                info.difficultyBeatmapSets.flatMap { bms ->
                    bms.difficultyBeatmaps.map { df ->
                        basePath.toPath().resolve(df.beatmapFilename) to bms.characteristicName + df.difficulty
                    }
                }
            paths.map { pathInfo ->
                val f = pathInfo.first.toFile()
                try {
                    f.source().buffer().use {
                        val ba = ByteArray(8182)
                        while (!it.exhausted()) {
                            val cnt = it.read(ba)
                            if (cnt == -1) break
                            val sub = ba.sliceArray(0 until cnt)
                            md.update(sub)
                        }
                    }
                    // will cause oom when json file is too large
                    // some chroma maps larger than 10MB, even 30MB
                    // in android, it will cause oom
                    val stream1 = f.inputStream()
                    try {
                        val v2 = json.decodeFromStream<BSDifficulty>(stream1)
                        v2DifficultyMap[pathInfo.second] = v2
                    } catch (e: Exception) {
                        val stream2 = f.inputStream()
                        try {
                            val v3 = json.decodeFromStream<BSDifficultyV3>(stream2)
                            v3DifficultyMap[pathInfo.second] = v3
                        } finally {
                            stream2.close()
                        }
                    } finally {
                        stream1.close()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    throw Exception("${file.absolutePath}: ${pathInfo.second} cannot be decoded")
                }
            }
            val sha1 = md.digest()
            val fx = "%0" + md.digestLength * 2 + "x"
            val hash = String.format(fx, BigInteger(1, sha1))
            val extractedMapInfo =
                ExtractedMapInfo(
                    mapId = mapId,
                    hash = hash,
                    mapInfo = info,
                    songDuration = 0.0,
                    basePath = basePath.toPath(),
                    infoFilename = infoFile.name,
                    songFilename = info.songFilename,
                    coverFilename = info.coverFilename,
                    v2MapObjectMap = v2DifficultyMap,
                    v3MapObjectMap = v3DifficultyMap,
                )
            return extractedMapInfo
        }

        @OptIn(ExperimentalSerializationApi::class)
        fun extractMapInfoFromDirV2(mapPath: Path): IExtractedMapInfo {
            val pattern = Regex("^[0-9a-f]{1,5} \\(.+\\)$")
            val mapId =
                if (pattern.matches(mapPath.name)) {
                    mapPath.name.substring(0, mapPath.name.indexOf(" (")).trim()
                } else {
                    null
                }
            val (hash, err) = mapDigest(mapPath)
            if (err != null) {
                return IExtractedMapInfo.ErrorMapInfo(hash, mapId, mapPath, null, err)
            }
            // means hash has been calculated and verified
            val files = FileSystem.SYSTEM.list(mapPath)
            val infoFilePath = files.find { it.name.lowercase() == "info.dat" }!!
            val infoContent = FileSystem.SYSTEM.read(infoFilePath) { this.readUtf8() }
            val info = json.decodeFromString<FSMapInfo>(infoContent)

            val paths =
                info.difficultyBeatmapSets.flatMap { bms ->
                    bms.difficultyBeatmaps.map { df ->
                        mapPath.resolve(df.beatmapFilename) to bms.characteristicName + df.difficulty
                    }
                }
            val v2DifficultyMap = mutableMapOf<String, BSDifficulty>()
            val v3DifficultyMap = mutableMapOf<String, BSDifficultyV3>()

            paths.map { pathInfo ->
                try {
                    val size = FileSystem.SYSTEM.metadata(pathInfo.first).size
                    if (size!! > 5 * 1024 * 1024) {
                        throw ScannerException.JSONFileTooLargeException("File size is too large", mapId = mapId, mapPath = pathInfo.first)
                    }
                    val f = pathInfo.first.toFile()
                    // will cause oom when json file is too large
                    // some chroma maps larger than 10MB, even 30MB
                    // in android, it will cause oom
                    val stream1 = f.inputStream()
                    try {
                        val v2 = json.decodeFromStream<BSDifficulty>(stream1)
                        v2DifficultyMap[pathInfo.second] = v2
                    } catch (e: Exception) {
                        val stream2 = f.inputStream()
                        try {
                            val v3 = json.decodeFromStream<BSDifficultyV3>(stream2)
                            v3DifficultyMap[pathInfo.second] = v3
                        } finally {
                            stream2.close()
                        }
                    } finally {
                        stream1.close()
                    }
                } catch (e: ScannerException.JSONFileTooLargeException) {
                    return IExtractedMapInfo.ErrorMapInfo(hash, mapId, mapPath, info, e)
                } catch (e: Exception) {
                    return IExtractedMapInfo.ErrorMapInfo(
                        hash,
                        mapId,
                        mapPath,
                        info,
                        ScannerException.ParseException(
                            "$pathInfo can not parse as beatsaber map",
                            mapId = mapId,
                            mapDir = pathInfo.first.toString(),
                        ),
                    )
                }
            }
            return if (mapId != null) {
                IExtractedMapInfo.BSMapInfo(
                    hash = hash,
                    mapId = mapId,
                    mapInfo = info,
                    mapPath = mapPath,
                    name = info.songName,
                    infoFilename = infoFilePath.name,
                    songFilename = info.songFilename,
                    coverFilename = info.coverFilename,
                    v2MapObjectMap = v2DifficultyMap,
                    v3MapObjectMap = v3DifficultyMap,
                )
            } else {
                IExtractedMapInfo.LocalMapInfo(
                    hash = hash,
                    mapPath = mapPath,
                    name = info.songName,
                    infoFilename = infoFilePath.name,
                    songFilename = info.songFilename,
                    coverFilename = info.coverFilename,
                    mapInfo = info,
                    v2MapObjectMap = v2DifficultyMap,
                    v3MapObjectMap = v3DifficultyMap,
                )
            }
        }
    }
}
