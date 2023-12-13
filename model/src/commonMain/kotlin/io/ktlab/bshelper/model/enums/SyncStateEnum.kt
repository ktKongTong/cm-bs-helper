package io.ktlab.bshelper.model.enums

enum class SyncStateEnum(val value: String) {
    SYNCED("SYNCED"),
    SYNCING("SYNCING"),
    UN_SYNCED("UN_SYNCED");
    companion object {
        fun from(value: String): SyncStateEnum {
            println(value)
            return when (value) {
                "SYNCED" -> SYNCED
                "SYNCING" -> SYNCING
                "UN_SYNCED" -> UN_SYNCED
                else -> throw IllegalArgumentException()
            }
        }
    }
}