package io.ktlab.bshelper.model.enums

import io.ktlab.bshelper.model.IMap


enum class SortKey(val human: String, val slug: String) {
    DEFAULT("默认", "default"),
    NPS("NPS", "nps"),
    DURATION("时长", "duration"),
    NOTES("方块数", "notes");
    //    BLOCKS,
    override fun toString(): String {
        return human
    }

    companion object {
        private val map = entries.associateBy(SortKey::slug)
        fun fromSlug(slug: String) = map[slug]
        val allSortKeys = entries.toTypedArray()
    }
}


enum class SortType {
    ASC,
    DESC;
    fun reverse(): SortType {
        return if (this == ASC) DESC else ASC
    }
}

fun SortKey.getSortKey(): (IMap) -> Comparable<*> {
    return when (this) {
        SortKey.DEFAULT -> { it -> it.getID() }
        SortKey.NPS -> { it -> it.getMaxNPS() }
        SortKey.DURATION -> { it -> it.getDuration() }
        SortKey.NOTES -> { it -> it.getMaxNotes() }
//        SortKey.BLOCKS -> { it -> it.get() }
    }
}

fun SortType.getCompareByFunction() : (Comparable<*>) -> Comparator<Comparable<*>> {
    return when (this) {
        SortType.ASC -> { x -> compareBy() }
        SortType.DESC -> { x -> compareBy() }
    }
}

fun SortKey.getSortKeyComparator(sortType: SortType): Comparator<IMap> {
    return when (this) {
        SortKey.DEFAULT -> if (sortType == SortType.ASC) compareBy { it.getID() } else compareByDescending { it.getID() }
        SortKey.NPS -> if (sortType == SortType.ASC) compareBy { it.getMaxNPS() } else compareByDescending { it.getMaxNPS() }
        SortKey.DURATION -> if (sortType == SortType.ASC) compareBy { it.getDuration() } else compareByDescending { it.getDuration() }
        SortKey.NOTES -> if (sortType == SortType.ASC) compareBy { it.getMaxNotes() } else compareByDescending { it.getMaxNotes() }
    }
}

fun Pair<SortKey, SortType>.getSortKeyComparator(): Comparator<IMap> {
    return this.first.getSortKeyComparator(this.second)
}