package io.ktlab.kown

import io.ktlab.kown.database.DBHelper
import io.ktlab.kown.database.NoOpsDBHelper

data class KownConfig(
    var databaseEnabled: Boolean = false,
    var retryEnabled: Boolean = false,
    var retryCount: Int = 3,
    var chunkSize: Int = 4.MB,
    var concurrentDownloads: Int = 5,
    var dbHelper: DBHelper = NoOpsDBHelper,
)