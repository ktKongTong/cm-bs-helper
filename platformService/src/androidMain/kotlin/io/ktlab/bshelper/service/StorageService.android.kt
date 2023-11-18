package io.ktlab.bshelper.service

import android.content.Context
import okio.Path
import okio.Path.Companion.toPath

actual class StorageService(
    private val context: Context
) {

    actual fun getTempDir(): Path {
        val tmpDir = context.filesDir.resolve("tmp").also {
            if (!it.exists()) {
                it.mkdirs()
            }
        }
        return tmpDir.absolutePath.toPath()
    }

    actual fun getDownloadDir(): Path {
        val tmpDir = context.filesDir.resolve("tmp").also {
            if (!it.exists()) {
                it.mkdirs()
            }
        }
        return tmpDir.absolutePath.toPath()
    }

}