package io.ktlab.bshelper.utils

import io.ktlab.bshelper.model.BSMapVersion
import io.ktlab.bshelper.model.FSMap
import io.ktlab.bshelper.model.MapDifficulty
import io.ktlab.bshelper.model.enums.ECharacteristic
import io.ktlab.bshelper.model.enums.EMapDifficulty
import io.ktlab.bsmg.beatmapv2.V2BeatMapObject
import io.ktlab.bsmg.beatmapv3.V3BeatMapObject
import io.ktlab.bsmg.FSMapDifficulty
import io.ktlab.bsmg.FSMapInfo
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.sink
import okio.source
import java.io.ByteArrayOutputStream
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.UUID
import kotlin.time.DurationUnit
import kotlin.time.toDuration


data class ExtractedMapInfo (
    val mapId:String? = null,
    val hash: String,
    val mapInfo: FSMapInfo,
    val songDuration: Double = 0.0,
    val basePath: Path,
    val infoFilename: String? = null,
    val songFilename: String? = null,
    val coverFilename: String? = null,
    val v2MapObjectMap: Map<String,V2BeatMapObject>? = null,
    val v3MapObjectMap: Map<String,V3BeatMapObject>? = null,
) {
    fun generateFSMapDBO(playlistId: String):FSMap{
        return FSMap(
            mapId = mapId ?: "",
            version = mapInfo.version,
            name = mapInfo.songName,
            author = mapInfo.songAuthorName,
            duration = songDuration.toDuration(DurationUnit.SECONDS),
            relativeCoverPath = coverFilename ?: "",
            relativeSongPath = songFilename ?: "",
            relativeInfoPath = infoFilename ?: "",
            dirFilename = basePath.name,
            playlistBasePath = basePath.toFile().parentFile?.absolutePath ?: "",
            hash = hash,
            playlistId = playlistId,
        )
    }
    fun generateVersionDBO():BSMapVersion{
        return BSMapVersion(
            mapId = mapId ?: "",
            hash = hash,
            state = "UN_SYNCED",
            key = "",
            createdAt = null,
            sageScore = 0,
            downloadURL = "",
            coverURL = "",
            previewURL = "",
        )
    }

}

class BSMapUtils {
    companion object {
        private val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        fun checkIfBSMap(file: File): Boolean {
            if (file.isDirectory) {
                return false
            }
            return file.listFiles()?.any {it != null && it.name.lowercase() == "info.dat" } ?: false
        }

        @OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
        fun extractMapInfoFromDir(playlistAbsolutePath:String, file: File, playlistId:String): ExtractedMapInfo {
//            dirPath must be a map directory
            val basePath = file.absolutePath
            val mapId: String
            try {
                mapId = file.name.substring(0, file.name.indexOf(" (")).trim()
            }catch (e:Exception){ throw Exception("${file.absolutePath}: cannot extract mapId") }
            val files = file.listFiles()  ?: throw Exception("${file.absolutePath}: File list is null")
            val infoFile = files.find {it.name.lowercase() == "info.dat" } ?: throw Exception("${file.absolutePath}: Info.dat or info.dat not found")

            val md = MessageDigest.getInstance("SHA1")
            val info = json.decodeFromStream<FSMapInfo>(infoFile.inputStream())
            val bytes = ByteArrayOutputStream()
            val v2DifficultyMap = mutableMapOf<String, V2BeatMapObject>()
            val v3DifficultyMap = mutableMapOf<String, V3BeatMapObject>()
            bytes.sink().buffer()
                .use {
                    it.writeAll(infoFile.source())
                    info.difficultyBeatmapSets.forEach{bms ->
                        bms.difficultyBeatmaps.forEach {df ->
                            basePath.toPath()
                                .resolve(df.beatmapFilename)
                                .toFile()
                                .source()
                                .buffer()
                                .use {source ->
                                    val string = source.readUtf8()
                                    try {
                                        if (string.contains("_version")){
                                            val v2BeatMap = json.decodeFromString<V2BeatMapObject>(string)
                                            v2DifficultyMap[bms.characteristicName+df.difficulty] = v2BeatMap
                                        }else {
                                            val v3BeatMap = json.decodeFromString<V3BeatMapObject>(string)
                                            v3DifficultyMap[bms.characteristicName+df.difficulty] = v3BeatMap
                                        }
                                    }catch (e:Exception){
                                        throw Exception("${file.absolutePath}: ${df.beatmapFilename} cannot be decoded")
                                    }
                                    it.write(string.toByteArray())
                                }
                        }
                    }
                }
            val sha1 = md.digest(bytes.toByteArray())
            val fx = "%0" + md.digestLength * 2 + "x"
            val hash = String.format(fx, BigInteger(1, sha1))
           val extractedMapInfo = ExtractedMapInfo(
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
        fun extractHash(
            basePath: Path,
        ) {
            val md = MessageDigest.getInstance("SHA1")
            val info = basePath.resolve("Info.dat").toFile().inputStream().use {
//                val byteArrayOutputStream = ByteArrayOutputStream()
//                it.copyTo(byteArrayOutputStream, sizeLimit = 50 * 1024 * 1024)
                json.decodeFromStream<io.beatmaps.common.beatsaber.MapInfo>(it)
            }

            var bytes = basePath.resolve("Info.dat").toFile().readBytes()
            info._difficultyBeatmapSets.forEach{bms ->
                bms._difficultyBeatmaps.forEach {df ->
                    val f = basePath.resolve(df._beatmapFilename).toFile()
                    f.readBytes().let {
                        bytes += it
                    }
                }
            }
            val sha1 = md.digest(bytes)
            val fx = "%0" + md.digestLength * 2 + "x"
            val digest = String.format(fx, BigInteger(1, sha1))
        }
    }
}

fun V2BeatMapObject.generateMapDifficultyInfo(
    extractedMapInfo: ExtractedMapInfo,
    characteristic:ECharacteristic,
    difficulty: FSMapDifficulty
):MapDifficulty {

    val notes = this._notes.count { it._type <= 1 }
    val bombs = this._notes.count { it._type == 3 }
    val obstacles = this._obstacles.count()
    return MapDifficulty(
        uuid = UUID.randomUUID().toString(),
        seconds = extractedMapInfo.songDuration,
        hash = extractedMapInfo.hash,
        mapId = extractedMapInfo.mapId ?: "",
        difficulty = EMapDifficulty.from(difficulty.difficulty),
        characteristic = characteristic,
        notes = notes.toLong(),
        nps = notes / extractedMapInfo.songDuration,
        njs = difficulty.noteJumpStartBeatOffset,
        bombs = bombs.toLong(),
        obstacles = obstacles.toLong(),
        offset = difficulty.noteJumpStartBeatOffset,
        events = this._events.count().toLong(),
        length = 0.0,
        chroma = null,
        me = null,
        ne = null,
        cinema = null,
        maxScore = null,
        label = null,
    )
}

fun V3BeatMapObject.generateMapDifficultyInfo(
    extractedMapInfo: ExtractedMapInfo,
    characteristic:ECharacteristic,
    difficulty: FSMapDifficulty
):MapDifficulty {
    val notes = this.burstSliders.count() + this.sliders.count() + this.colorNotes.count()
    val nps = notes / extractedMapInfo.songDuration
    val bombs = this.bombNotes.count()
    val obstacles = this.obstacles.count()
    val offset = difficulty.noteJumpStartBeatOffset

    return MapDifficulty(
        uuid = UUID.randomUUID().toString(),
        seconds = extractedMapInfo.songDuration,
        hash = extractedMapInfo.hash,
        mapId = extractedMapInfo.mapId ?: "",
        difficulty = EMapDifficulty.from(difficulty.difficulty),
        characteristic = characteristic,
        notes = notes.toLong(),
        nps = nps,
        njs = difficulty.noteJumpStartBeatOffset,
        bombs = bombs.toLong(),
        obstacles = obstacles.toLong(),
        offset = offset,
        events = 0,
        length = 0.0,
        chroma = null,
        me = null,
        ne = null,
        cinema = null,
        maxScore = null,
        label = null,
    )
}