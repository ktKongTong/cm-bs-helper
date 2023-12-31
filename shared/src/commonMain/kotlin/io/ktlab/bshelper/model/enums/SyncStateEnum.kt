package io.ktlab.bshelper.model.enums

enum class SyncStateEnum(val value: String) {
    SYNCED("SYNCED"),
    SYNCING("SYNCING"),
    UN_SYNCED("UN_SYNCED"),
    ;

    companion object {
        fun from(value: String): SyncStateEnum {
            return when (value) {
                "SYNCED" -> SYNCED
                "SYNCING" -> SYNCING
                "UN_SYNCED" -> UN_SYNCED
                else -> throw IllegalArgumentException()
            }
        }
    }

    override fun toString(): String {
        return value
    }
}
