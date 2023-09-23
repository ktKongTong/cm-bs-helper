package io.ktlab.bshelper.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import io.ktlab.bshelper.model.UserPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferenceRepository (
    private val dataStore: DataStore<Preferences>
){
    companion object {
        const val DEFAULT_CURRENT_MANAGE_DIR = ""
        const val DEFAULT_CURRENT_PLAYLIST_ID = ""
    }

    private val currentManageDirKey = stringPreferencesKey("currentManageDir")
    private val currentPlaylistIdKey = stringPreferencesKey("currentPlaylistId")

    private val preferenceFlow: Flow<UserPreference> = dataStore.data.map {
        UserPreference(
            it[currentManageDirKey] ?: DEFAULT_CURRENT_MANAGE_DIR,
            it[currentPlaylistIdKey] ?: DEFAULT_CURRENT_PLAYLIST_ID,
        )
    }

    fun getUserPreference(): Flow<UserPreference> = preferenceFlow

    suspend fun changeCurrentManageDir(currentManageDir: String) {
        dataStore.edit {
            it[currentManageDirKey] = currentManageDir
        }
    }

//    suspend fun changeCurrentPlaylistId(currentPlaylistId: String) {
//        dataStore.edit {
//            it[currentPlaylistIdKey] = currentPlaylistId
//        }
//    }

    suspend fun updateUserPreference(userPreference: UserPreference) {
        dataStore.edit {
            it[currentManageDirKey] = userPreference.currentManageDir
            it[currentPlaylistIdKey] = userPreference.currentPlaylistId
        }
    }

}