package io.ktlab.bshelper.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.ktlab.bshelper.model.UserPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferenceRepository (
    private val dataStore: DataStore<Preferences>
){
    companion object {
        const val DEFAULT_CURRENT_MANAGE_DIR = ""
    }

    private val currentManageDirKey = stringPreferencesKey("currentManageDir")
    private val currentThemeColorKey = stringPreferencesKey("currentThemeColor")
    private val currentThemeModeKey = stringPreferencesKey("currentThemeMode")
    private val preferenceFlow: Flow<UserPreference> = dataStore.data.map {
        UserPreference(
            it[currentManageDirKey] ?: DEFAULT_CURRENT_MANAGE_DIR,
            it[currentThemeColorKey] ?: "",
            it[currentThemeModeKey] ?: "",
        )
    }

    fun getUserPreference(): Flow<UserPreference> = preferenceFlow

    suspend fun changeCurrentManageDir(currentManageDir: String) {
        dataStore.edit {
            it[currentManageDirKey] = currentManageDir
        }
    }

    suspend fun changeCurrentThemeColor(currentThemeColor: String) {
        dataStore.edit {
            it[currentThemeColorKey] = currentThemeColor
        }
    }

    suspend fun changeCurrentThemeMode(currentThemeMode: String) {
        dataStore.edit {
            it[currentThemeModeKey] = currentThemeMode
        }
    }

    suspend fun updateUserPreference(userPreference: UserPreference) {
        dataStore.edit {
            it[currentManageDirKey] = userPreference.currentManageDir
        }
    }

}