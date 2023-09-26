package io.ktlab.bshelper.model

data class UserPreference(
    val currentManageDir: String = "",
    val currentPlaylistId: String = "",

){
    companion object {
        fun getDefaultUserPreference(): UserPreference {
            return UserPreference()
        }
    }
}