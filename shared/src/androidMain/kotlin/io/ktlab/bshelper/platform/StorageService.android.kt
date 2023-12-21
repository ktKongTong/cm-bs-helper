package io.ktlab.bshelper.platform

import android.content.Context
import android.os.Environment
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

actual class StorageService(
    private val context: Context,
) {
    actual fun getTempDir(): Path {
        val tmpDir =
            context.filesDir.resolve("tmp").also {
                if (!it.exists()) {
                    it.mkdirs()
                }
            }
        return tmpDir.absolutePath.toPath()
    }

    actual fun getDownloadDir(): Path {
        val tmpDir =
            context.filesDir.resolve("tmp").also {
                if (!it.exists()) {
                    it.mkdirs()
                }
            }
        return tmpDir.absolutePath.toPath()
    }

    actual fun getBackupDir(): Path {
        val backupDir = context.getExternalFilesDir(null)?.resolve("backup")

        if (!backupDir?.exists()!!) {
            backupDir.mkdirs()
        }
        return backupDir.absolutePath.toPath()
    }

    actual fun getBackupDir(dir: Path): Path {
        if (dir.toString().startsWith(Environment.getExternalStorageDirectory()?.absolutePath?:"")
            && !dir.toString()
                .startsWith(Environment.getExternalStorageDirectory()
                    ?.toPath()?.resolve("Android/data").toString())
            ) {
            // create backup dir in external storage
            val backupDir = Environment.getExternalStorageDirectory().path.toPath()
                    .resolve("Documents/BSHelper/backup")
            if(!FileSystem.SYSTEM.exists(backupDir)){
                FileSystem.SYSTEM.createDirectories(backupDir, mustCreate = true)
            }
            return backupDir
        }
        return getBackupDir()
    }
}
