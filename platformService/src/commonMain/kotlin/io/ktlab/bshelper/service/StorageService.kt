package io.ktlab.bshelper.service

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import io.ktlab.bshelper.model.UserPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File


expect class StorageService {

    fun getDownloadDir(): File

}