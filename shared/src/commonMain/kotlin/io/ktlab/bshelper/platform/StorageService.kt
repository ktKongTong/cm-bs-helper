package io.ktlab.bshelper.platform

import okio.Path

expect class StorageService {
    fun getTempDir(): Path

    fun getDownloadDir(): Path
}
