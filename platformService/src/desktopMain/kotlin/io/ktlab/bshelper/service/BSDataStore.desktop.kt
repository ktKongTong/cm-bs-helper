package io.ktlab.bshelper.service

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

fun createDataStore(): DataStore<Preferences> = getDataStore(
    producePath = { DesktopConfig.getConfigPath().toString()+"/"+dataStoreFileName }
)