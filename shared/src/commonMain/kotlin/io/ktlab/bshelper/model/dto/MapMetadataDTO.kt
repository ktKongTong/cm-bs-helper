package io.ktlab.bshelper.model.dto
//
// import io.ktkt.bshelper.data.model.room.MapMetadata
import kotlinx.serialization.Serializable

@Serializable
data class MapMetadataDTO(
    val bpm: Double,
    val duration: Long,
    val songName: String,
    val songSubName: String,
    val songAuthorName: String,
    val levelAuthorName: String,
) {
//    fun convertToEntity():MapMetadata{
//        return MapMetadata(
//            bpm = bpm,
//            duration = duration,
//            songName = songName,
//            songSubName = songSubName,
//            songAuthorName = songAuthorName,
//            levelAuthorName = levelAuthorName,
//        )
//    }
}
