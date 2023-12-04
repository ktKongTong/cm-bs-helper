package io.ktlab.bshelper.utils

import io.beatmaps.common.beatsaber.BSDifficulty
import io.beatmaps.common.beatsaber.BSDifficultyV3
import io.ktlab.bshelper.model.BSMapVersion
import io.ktlab.bshelper.model.FSMap
import io.ktlab.bshelper.model.MapDifficulty
import io.ktlab.bshelper.model.enums.ECharacteristic
import io.ktlab.bshelper.model.enums.EMapDifficulty
import io.ktlab.bsmg.FSMapDifficulty
import io.ktlab.bsmg.FSMapInfo
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.source
import okio.use
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
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
    val v2MapObjectMap: Map<String, BSDifficulty>? = null,
    val v3MapObjectMap: Map<String, BSDifficultyV3>? = null,
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
            active = true,
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
        @OptIn(ExperimentalSerializationApi::class)
        fun extractMapInfoFromDir(playlistAbsolutePath:String, file: File, playlistId:String): ExtractedMapInfo {
            val basePath = file.absolutePath
            val mapId: String
            try {
                mapId = file.name.substring(0, file.name.indexOf(" (")).trim()
            }catch (e:Exception){ throw Exception("${file.absolutePath}: cannot extract mapId") }

            val files = file.listFiles()  ?: throw Exception("${file.absolutePath}: File list is null")
            val infoFile = files.find {it.name.lowercase() == "info.dat" } ?: throw Exception("${file.absolutePath}: Info.dat or info.dat not found")

            val md = MessageDigest.getInstance("SHA1")
            md.reset()
            val infoContent = infoFile.readText()
            val info = json.decodeFromString<FSMapInfo>(infoContent)
            val v2DifficultyMap = mutableMapOf<String, BSDifficulty>()
            val v3DifficultyMap = mutableMapOf<String, BSDifficultyV3>()
            val infobytes = infoFile.readBytes()
            md.update(infobytes)
            val paths = info.difficultyBeatmapSets.flatMap {bms->
                bms.difficultyBeatmaps.map { df->
                    basePath.toPath().resolve(df.beatmapFilename) to bms.characteristicName+df.difficulty
                }
            }
            paths.map { pathInfo->
                val f = pathInfo.first.toFile()
                try {
                    f.source().buffer().use {
                        val ba = ByteArray(8182)
                        while (!it.exhausted()){
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
                    }catch (e: Exception){
                        val stream2 = f.inputStream()
                        try {
                            val v3 = json.decodeFromStream<BSDifficultyV3>(stream2)
                            v3DifficultyMap[pathInfo.second] = v3
                        }finally {
                            stream2.close()
                        }
                    } finally {
                        stream1.close()
                    }

                }catch (e:Exception){
                    e.printStackTrace()
                    throw Exception("${file.absolutePath}: ${pathInfo.second} cannot be decoded")
                }
            }
            val sha1 = md.digest()
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
    }
}

fun BSDifficulty.generateMapDifficultyInfo(
    extractedMapInfo: ExtractedMapInfo,
    characteristic:ECharacteristic,
    difficulty: FSMapDifficulty
):MapDifficulty {

    return MapDifficulty(
        uuid = UUID.randomUUID().toString(),
        seconds = extractedMapInfo.songDuration,
        hash = extractedMapInfo.hash,
        mapId = extractedMapInfo.mapId ?: "",
        difficulty = EMapDifficulty.from(difficulty.difficulty),
        characteristic = characteristic,
        notes = noteCount().toLong(),
        nps = noteCount() / extractedMapInfo.songDuration,
        njs = difficulty.noteJumpStartBeatOffset,
        bombs = bombCount().toLong(),
        obstacles = obstacleCount().toLong(),
        offset = difficulty.noteJumpStartBeatOffset,
        events = eventCount().toLong(),
        length = 0.0,
        chroma = null,
        me = null,
        ne = null,
        cinema = null,
        maxScore = null,
        label = null,
    )
}

fun BSDifficultyV3.generateMapDifficultyInfo(
    extractedMapInfo: ExtractedMapInfo,
    characteristic:ECharacteristic,
    difficulty: FSMapDifficulty
):MapDifficulty {
//    val notes = this.burstSliders.count() + this.sliders.count() + this.colorNotes.count()
    val nps = noteCount() / extractedMapInfo.songDuration
    val bombs = bombCount()
    val obstacles = obstacleCount()
    val offset = difficulty.noteJumpStartBeatOffset

    return MapDifficulty(
        uuid = UUID.randomUUID().toString(),
        seconds = extractedMapInfo.songDuration,
        hash = extractedMapInfo.hash,
        mapId = extractedMapInfo.mapId ?: "",
        difficulty = EMapDifficulty.from(difficulty.difficulty),
        characteristic = characteristic,
        notes = noteCount().toLong(),
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