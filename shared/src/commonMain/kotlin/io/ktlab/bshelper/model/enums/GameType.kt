package io.ktlab.bshelper.model.enums

enum class GameType(val human: String,val opsStrategy: OpsStrategy) {
    LightBand("光之乐团",OpsStrategy.BeatSaberLike),
    BeatKungFu("功夫节奏",OpsStrategy.BeatSaberLike),
    AudioTrip("动感音旅",OpsStrategy.AudioTrip),
    BeatSaberLike("类 BS",OpsStrategy.BeatSaberLike),
    OnShape("OnShape(墙来了)",OpsStrategy.OnShape),
    SynthRiders("SynthRiders(幻音骑士)",OpsStrategy.SynthRiders);

    companion object {
        fun from(value: String): GameType {
            return entries.find { it.name == value } ?: throw IllegalArgumentException("No enum constant $value")
        }
        fun fromHuman(value: String): GameType {
            return entries.find { it.human == value } ?: throw IllegalArgumentException("No enum constant $value")
        }
    }
}