package io.ktlab.bshelper.platform

import androidx.datastore.core.DataStore
import io.ktlab.bshelper.model.UserPreferenceV2

fun createDataStore(storageService: StorageService): DataStore<UserPreferenceV2> =
    getDataStore(
        producePath = { storageService.getConfigDir().resolve(dataStoreFileName).toString() },
    )
