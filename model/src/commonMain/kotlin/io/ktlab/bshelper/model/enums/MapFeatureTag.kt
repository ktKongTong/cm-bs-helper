package io.ktlab.bshelper.model.enums


enum class MapFeatureTagType(val color: String) {
    None(""), General("blue"), Requirement("green");
}
//Verified Mapper
enum class MapFeatureTag(val type: MapFeatureTagType, val human: String, val slug: String, val description: String = "") {

    None(MapFeatureTagType.None, "", "", ""),

    AI(MapFeatureTagType.General, "AI", "automapper", "query with this, the result will include AI generated map"),
    Curated(MapFeatureTagType.General, "Curated", "curated", "query with this only curated map will be shown."),
    Ranked(MapFeatureTagType.General, "Ranked", "ranked","query with this only ranked map will be shown."),
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