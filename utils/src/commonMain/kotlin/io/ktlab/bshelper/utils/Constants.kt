package io.ktlab.bshelper.utils

import app.cash.sqldelight.db.SqlDriver
import java.io.File

object Constants {
//     val BASE_URL = listOf(
//        "https://beatsaver.wgzeyu.vip/api/",
//        "https://beatsaver.com/api/"
//     )
    val TOOL_BASE_URL = "https://kv-store-five.vercel.app"
    const val DATABASE_NAME = "bshelper-db"
    const val DEFAULT_ZIP_DOWNLOAD_PATH = "/storage/emulated/0/Download/BSHelper"
    const val DEFAULT_MANAGER_DIR_PATH = "/storage/emulated/0/Android/data/com.StarRiverVR.LightBand/files/CustomMusic"
    const val DEFAULT_DOWNLOAD_DIR_PATH = "/storage/emulated/0/Android/data/com.StarRiverVR.LightBand/files/CustomMusic"
    const val SYSTEM_BASE_PATH = "/storage/emulated/0"
    const val THREE_HOURS = 3 * 60 * 60 * 1000
    const val ONE_DAY = 24 * 60 * 60 * 1000
    const val ONE_WEEK = 7 * 24 * 60 * 60 * 1000
    const val HALF_MONTH = 15 * 24 * 60 * 60 * 1000
}