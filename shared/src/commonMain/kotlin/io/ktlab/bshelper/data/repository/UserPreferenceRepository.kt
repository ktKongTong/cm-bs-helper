package io.ktlab.bshelper.data.repository

import androidx.datastore.core.DataStore
import io.ktlab.bshelper.model.BSAPIProvider
import io.ktlab.bshelper.model.ImageSource
import io.ktlab.bshelper.model.ThemeMode
import io.ktlab.bshelper.model.UserPreferenceV2
import kotlinx.coroutines.flow.Flow

class UserPreferenceRepository(
    private val dataStore: DataStore<UserPreferenceV2>,
) {
//    companion object {
//        const val DEFAULT_CURRENT_MANAGE_DIR = ""
//    }
//    private val currentManageDirKey = stringPreferencesKey("currentManageDir")
//    private val currentThemeColorKey = stringPreferencesKey("currentThemeColor")
//    private val currentThemeModeKey = stringPreferencesKey("currentThemeMode")
    private val preferenceFlow: Flow<UserPreferenceV2> = dataStore.data

    fun getUserPreference(): Flow<UserPreferenceV2> = preferenceFlow

    suspend fun updateCurrentManageDir(currentManageDir: String) {
        dataStore.updateData { it.copy(currentManageDir = currentManageDir) }
    }

    suspend fun updateThemeColor(themeColor: Long) {
        dataStore.updateData { it.copy(themeColor = themeColor) }
    }

    suspend fun updateThemeMode(themeMode: ThemeMode) {
        dataStore.updateData { it.copy(themeMode = themeMode) }
    }
    suspend fun updateBSAPI(bsApiProvider: BSAPIProvider) {
        dataStore.updateData { it.copy(bsApiProvider = bsApiProvider) }
    }
    suspend fun updateImageSource(type:ImageSource,source: String?) {
        dataStore.updateData { it.copy(imageSource = type) }
    }
    suspend fun updateUserPreference(userPreference: UserPreferenceV2) {
        dataStore.updateData {userPreference }
    }
}
