package io.ktlab.bshelper.platform

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import io.ktlab.bshelper.model.UserPreferenceV2
import io.ktlab.bshelper.model.UserPreferencesSerializer
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.SynchronizedObject
import okio.Path.Companion.toPath

private lateinit var dataStore: DataStore<UserPreferenceV2>

@OptIn(InternalCoroutinesApi::class)
private val lock = SynchronizedObject()

fun getDataStore(producePath: () -> String): DataStore<UserPreferenceV2> =
    synchronized(lock) {
        if (::dataStore.isInitialized) {
            dataStore
        } else {
            val path = producePath().toPath().toFile()
            DataStoreFactory.create(
                produceFile = { path },
                serializer = UserPreferencesSerializer
            )
//            PreferenceDataStoreFactory.create {  }
//            PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })
//                .also { dataStore = it }
        }
    }

internal val dataStoreFileName = "bs-helper-perference.json"
