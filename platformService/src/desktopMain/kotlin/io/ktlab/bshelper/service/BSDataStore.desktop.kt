package io.ktlab.bshelper.service

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

fun createDataStore(
    storageService: StorageService
): DataStore<Preferences> = getDataStore(
    producePath = { storageService.getConfigDir().resolve(dataStoreFileName).toString() }
)