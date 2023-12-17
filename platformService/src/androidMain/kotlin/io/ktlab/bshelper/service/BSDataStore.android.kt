package io.ktlab.bshelper.service

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

fun createDataStore(context: Context): DataStore<Preferences> =
    getDataStore(
        producePath = { context.filesDir.resolve(dataStoreFileName).absolutePath },
    )
