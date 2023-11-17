package io.ktlab.bshelper.service

import android.content.Context
import android.os.Environment
import java.io.File

actual class StorageService(
    private val context: Context
) {

    fun getTempDir(): File {
        val tmpDir = context.filesDir.resolve("tmp").also {
            if (!it.exists()) {
                it.mkdirs()
            }
        }
        return tmpDir
    }

    actual fun getDownloadDir(): File {
        val tmpDir = context.filesDir.resolve("tmp").also {
            if (!it.exists()) {
                it.mkdirs()
            }
        }
        return tmpDir
//
//        return context.cacheDir
    }

}