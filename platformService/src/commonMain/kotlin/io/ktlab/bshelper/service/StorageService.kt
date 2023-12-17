package io.ktlab.bshelper.service

import okio.Path

expect class StorageService {
    fun getTempDir(): Path

    fun getDownloadDir(): Path
}
