package io.ktlab.bshelper.model

data class UserPreference(
    val currentManageDir: String = "",
    val currentThemeColor: String = "",
    val currentThemeMode: String = "",
) {
    companion object {
        fun getDefaultUserPreference(): UserPreference {
            return UserPreference()
        }
    }

    // to hex color like 0x00EDEFFF
    fun getThemeColor(): Long? {
        if (currentThemeColor.length != 10 || !Regex("0x[0-9A-Fa-f]{8}").matches(currentThemeColor)) {
            return null
        }
        return currentThemeColor.substring(2).toLong(16)
    }
}
