package io.ktlab.bshelper.model.vo

import io.ktlab.bshelper.model.enums.EMapDifficulty

class MapDiff(private var diff: Int) {
    companion object {
        const val EASY = 0b000001
        const val NORMAL = 0b000010
        const val HARD = 0b000100
        const val EXPERT = 0b001000
        const val EXPERTPLUS = 0b010000
        fun build(): MapDiff {
            return MapDiff(0b000000)
        }
        fun getDiff(diff: String): Int {
            return when (diff) {
                "Easy" -> EASY
                "Normal" -> NORMAL
                "Hard" -> HARD
                "Expert" -> EXPERT
                "ExpertPlus" -> EXPERTPLUS
                else -> 0
            }
        }
    }

    fun addDiff(diffs:List<EMapDifficulty>): MapDiff {
        for (diff in diffs) {
            when (diff) {
                EMapDifficulty.Easy -> addEasy()
                EMapDifficulty.Normal -> addNormal()
                EMapDifficulty.Hard -> addHard()
                EMapDifficulty.Expert -> addExpert()
                EMapDifficulty.ExpertPlus -> addExpertPlus()
            }
        }
        return this
    }
    fun addEasy(): MapDiff {
        diff = diff or EASY
        return this
    }
    fun addNormal(): MapDiff {
        diff = diff or NORMAL
        return this
    }
    fun addHard(): MapDiff {
        diff = diff or HARD
        return this
    }
    fun addExpert(): MapDiff {
        diff = diff or EXPERT
        return this
    }
    fun addExpertPlus(): MapDiff {
        diff = diff or EXPERTPLUS
        return this
    }

    fun hasEasy(): Boolean {
        return diff and EASY == EASY
    }
    fun hasNormal(): Boolean {
        return diff and NORMAL == NORMAL
    }
    fun hasHard(): Boolean {
        return diff and HARD == HARD
    }
    fun hasExpert(): Boolean {
        return diff and EXPERT == EXPERT
    }
    fun hasExpertPlus(): Boolean {
        return diff and EXPERTPLUS == EXPERTPLUS
    }
}