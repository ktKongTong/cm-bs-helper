package io.ktlab.bshelper.utils

// import io.ktlab.bsmg.FSMapInfo
import io.beatmaps.common.beatsaber.BSDifficulty
import io.beatmaps.common.beatsaber.BSDifficultyV3
import io.ktlab.bshelper.model.FSPlaylist
import io.ktlab.bshelper.model.bsmg.FSMapInfo
import io.ktlab.bshelper.model.enums.SyncStateEnum
import io.ktlab.bshelper.model.scanner.IExtractedMapInfo
import io.ktlab.bshelper.model.scanner.ScannerException
import kotlinx.datetime.Clock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okio.Buffer
import okio.FileSystem
import okio.HashingSink
import okio.Path
import okio.blackholeSink
import okio.buffer
import okio.source

fun newFSPlaylist(
    name: String,
    manageDirId:Long,
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
        manageDirId = manageDirId,
    )
}
fun newFSPlaylist(
    basePath: String,
    manageDirId:Long,
    name: String = "",
    bsPlaylistId: Int? = null,
    description: String? = null,
    topPlaylist: Boolean = false,
    customTags: String? = null,
): FSPlaylist {
    return FSPlaylist(
        id = basePath,
        name = name,
        description = description,
        sync = SyncStateEnum.SYNCED,
        bsPlaylistId = bsPlaylistId,
        basePath = basePath,
        syncTimestamp = Clock.System.now().epochSeconds,
        customTags = customTags,
        topPlaylist = topPlaylist,
        manageDirId = manageDirId,
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

        fun mapDigest(path: Path): Pair<String, ScannerException?> {
            val files = FileSystem.SYSTEM.list(path)
            val infoFile = files.find { it.name.lowercase() == "info.dat" }
                    ?: return "" to ScannerException.FileMissingException("Info.dat or info.dat not found", mapDir = path.toString())
            HashingSink.sha1(blackholeSink()).use { sink ->
                val paths = infoFile.toFile().source().buffer().use inner@{ source ->
                        val copySink = Buffer()
                        source.readAll(copySink)
                        val str = copySink.copy().readUtf8()
                        copySink.readAll(sink)
                        val info = json.decodeFromString<FSMapInfo>(str)
                        val paths =
                            info.difficultyBeatmapSets.flatMap { bms ->
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
                    v2MapObjectMap = v2DifficultyMap,
                    v3MapObjectMap = v3DifficultyMap,
                )
            } else {
                IExtractedMapInfo.LocalMapInfo(
                    hash = hash,
                    mapPath = mapPath,
                    name = info.songName,
                    infoFilename = infoFilePath.name,
                    mapInfo = info,
                    v2MapObjectMap = v2DifficultyMap,
                    v3MapObjectMap = v3DifficultyMap,
                )
            }
        }
    }
}
