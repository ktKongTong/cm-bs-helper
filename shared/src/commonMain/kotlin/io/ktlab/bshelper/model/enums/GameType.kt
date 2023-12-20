package io.ktlab.bshelper.model.enums

enum class GameType(val human: String? = null) {
    LightBand("光之乐团"),
    BeatKungFu("功夫节奏"),
    AudioTrip("动感音旅"),
    BeatSaberLike("类 BeatSaber"),
    OnShape("OnShape(墙来了)"),
    SynthRiders("SynthRiders(幻音骑士)");

    companion object {
        fun from(value: String): GameType {
            return entries.find { it.name == value } ?: throw IllegalArgumentException("No enum constant $value")
        }
    }
}