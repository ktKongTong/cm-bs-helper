package io.ktlab.bshelper.platform

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.ktlab.bshelper.platform.StorageService
import io.ktlab.bshelper.platform.dataStoreFileName
import io.ktlab.bshelper.platform.getDataStore

fun createDataStore(storageService: StorageService): DataStore<Preferences> =
    getDataStore(
        producePath = { storageService.getConfigDir().resolve(dataStoreFileName).toString() },
    )
