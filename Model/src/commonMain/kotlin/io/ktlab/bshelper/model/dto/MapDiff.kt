package io.ktlab.bshelper.model.dto

import io.ktlab.bshelper.model.enums.ECharacteristic
import io.ktlab.bshelper.model.enums.EMapDifficulty
import kotlinx.serialization.Serializable
@Serializable
data class MapDiffDTO(
    val njs: Double,
    val offset: Double,
    val notes: Long,
    val bombs: Long,
    val obstacles: Long,
    val nps: Double,
    val length: Double,
    val characteristic: ECharacteristic,
    val difficulty: EMapDifficulty,
    val events: Long,
    val chroma: Boolean,
    val me: Boolean,
    val ne: Boolean,
    val cinema: Boolean,
    val seconds: Double,
    val paritySummary: ParitySummary,
    val maxScore: Long,
    val label: String? = null,
){

//    fun convertToEntity( hash:String, mapId:String,uuid: String = ""):MapDifficulty{
//        return MapDifficulty(
//            uuid = uuid,
//            seconds = seconds,
//            hash = hash,
//            mapId = mapId,
//            difficulty = difficulty,
//            characteristic = characteristic,
//            notes = notes,
//            notePerSecond = nps,
//            noteJumpSpeed = njs,
//            bombs = bombs,
//            obstacles = obstacles,
//            offset = offset,
//            events = events,
//            chroma = chroma,
//            length = length,
//            mapExtension = me,
//            noodleExtension = ne,
//            cinema = cinema,
//            paritySummary = paritySummary.convertToEntity(),
//            maxScore = maxScore,
//        )
//    }
}
@Serializable
data class ParitySummary(
    val errors: Long,
    val warns: Long,
    val resets: Long,
){
//    fun convertToEntity(): RoomParitySummary {
//        return RoomParitySummary(
//            errors = errors,
//            warns = warns,
//            resets = resets,
//        )
//    }
}