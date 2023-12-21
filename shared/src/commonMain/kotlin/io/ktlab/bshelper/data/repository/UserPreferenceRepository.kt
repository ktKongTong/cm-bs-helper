package io.ktlab.bshelper.data.repository

import androidx.datastore.core.DataStore
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktlab.bshelper.model.BSAPIProvider
import io.ktlab.bshelper.model.ImageSource
import io.ktlab.bshelper.model.SManageFolder
import io.ktlab.bshelper.model.ThemeMode
import io.ktlab.bshelper.model.UserPreferenceV2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private val logger = KotlinLogging.logger {}

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

    private val preferenceJob = Job()
    private val repositoryScope = CoroutineScope(Dispatchers.IO + preferenceJob)

    private lateinit  var _preference:UserPreferenceV2

    init {
        repositoryScope.launch {
            preferenceFlow.collect {
                _preference = it
            }
        }
    }

    fun getUserPreference(): Flow<UserPreferenceV2> = preferenceFlow

    fun getCurrentUserPreference(): UserPreferenceV2 {
        return _preference
    }
    suspend fun updateCurrentManageFolder(currentManageFolder: SManageFolder?) {
        dataStore.updateData { it.copy(currentManageFolder = currentManageFolder) }
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
