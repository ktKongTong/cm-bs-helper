package io.ktlab.bshelper.model.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

enum class EMapDifficulty(val short: String, val human: String, val slug: String, val alias: List<String>) {
    Easy("E", "Easy", "easy", listOf("Easy", "easy")),
    Normal("N", "Normal", "normal", listOf("Normal", "normal")),
    Hard("H", "Hard", "hard", listOf("Hard", "hard")),
    Expert("Ex", "Expert", "expert", listOf("Expert", "expert")),
    ExpertPlus("Ex+", "ExpertPlus", "expert-plus", listOf("ExpertPlus", "Expert+", "expert-plus")),
    ;

    companion object {
        private val map = entries.associateBy(EMapDifficulty::slug)
        private val name = entries.associateBy(EMapDifficulty::alias).flatMap { (key, value) -> value.alias.map { it to value } }.toMap()

        fun from(string: String): EMapDifficulty {
            return name [string] ?: throw IllegalArgumentException("Unknown difficulty: $string")
        }

        fun toString(eMapDifficulty: EMapDifficulty): String {
            return eMapDifficulty.human
        }
    }
}

@Serializable(with = ECharacteristicSerializable::class)
enum class ECharacteristic(val short: String, val human: String, val slug: String) {
    Standard("S", "Standard", "Standard"),
    OneSaber("1", "One Saber", "OneSaber"),
    NoArrows("NA", "No Arrows", "NoArrows"),
    _90_DEGREE("90", "90 Degree", "90Degree"),
    _360_DEGRE("360", "360 Degree", "360Degree"),
    Lawless("L", "Lawless", "Lawless"),
    Lightshow("LS", "Lightshow", "Lightshow"),
    Legacy("L", "Legacy", "Legacy"),
    ;

    companion object {
        private val map = entries.associateBy(ECharacteristic::slug)

        fun from(string: String): ECharacteristic {
            return map[string] ?: throw IllegalArgumentException("Unknown characteristic: $string")
        }

        fun toString(eCharacteristic: ECharacteristic): String {
            return eCharacteristic.slug
        }
    }
}

class ECharacteristicSerializable : KSerializer<ECharacteristic> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("MapTag", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ECharacteristic = ECharacteristic.from(decoder.decodeString())

    override fun serialize(
        encoder: Encoder,
        value: ECharacteristic,
    ) = encoder.encodeString(value.toString())
}
