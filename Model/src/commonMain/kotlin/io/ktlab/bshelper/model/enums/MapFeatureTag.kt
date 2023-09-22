package io.ktlab.bshelper.model.enums


enum class MapFeatureTagType(val color: String) {
    None(""), General("blue"), Requirement("green");
}
//Verified Mapper
enum class MapFeatureTag(val type: MapFeatureTagType, val human: String, val slug: String) {

    None(MapFeatureTagType.None, "", ""),

    AI(MapFeatureTagType.General, "AI", "automapper"),
    Curated(MapFeatureTagType.General, "Curated", "curated"),
    Ranked(MapFeatureTagType.General, "Ranked", "ranked"),
    FullSpread(MapFeatureTagType.General, "Full Spread", "fullSpread"),
    VerifiedMapper(MapFeatureTagType.General, "Verified Mapper", "verified"),

    Chroma(MapFeatureTagType.Requirement, "Chroma", "chroma"),
    Noodle(MapFeatureTagType.Requirement, "Noodle", "noodle"),
    MappingExtensions(MapFeatureTagType.Requirement, "Mapping Extensions", "me"),
    Cinema(MapFeatureTagType.Requirement, "Cinema", "cinema");

    companion object {
        private val map = entries.associateBy(MapFeatureTag::slug)
        fun fromSlug(slug: String) = map[slug]
        val mapFeatureTags = entries.filter { it.type != MapFeatureTagType.None }
        val generalMapFeatureTags = entries.filter { it.type == MapFeatureTagType.General }
        val requirementMapFeatureTags = entries.filter { it.type == MapFeatureTagType.Requirement }

        val sorted = MapTag.entries.toTypedArray().sortedWith(compareBy({ it.type.ordinal }, { it.human }))
    }
}