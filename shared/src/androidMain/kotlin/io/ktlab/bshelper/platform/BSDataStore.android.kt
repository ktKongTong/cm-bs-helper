package io.ktlab.bshelper.platform

import android.content.Context
import androidx.datastore.core.DataStore
import io.ktlab.bshelper.model.UserPreferenceV2

fun createDataStore(context: Context): DataStore<UserPreferenceV2> =
    getDataStore(
        producePath = { context.filesDir.resolve(dataStoreFileName).absolutePath },
    )
