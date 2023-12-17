package io.ktlab.bshelper.service

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

actual class StorageService {

//    private
    //
    private val homeDir = System.getProperty("user.home") ?: throw IllegalStateException("can't get user home dir")
    private var appDir : Path = homeDir.toPath().resolve(".bshelper")
    private var configDir: Path = appDir.resolve("config")
    private var tmpDir: Path = appDir.resolve("tmp")
    init {

        synchronized(this){
            if (!FileSystem.SYSTEM.exists(appDir)) {
                FileSystem.SYSTEM.createDirectory(appDir,mustCreate = true)
            }
            if (!FileSystem.SYSTEM.exists(configDir)) {
                FileSystem.SYSTEM.createDirectory(configDir,mustCreate = true)
            }
            if (!FileSystem.SYSTEM.exists(tmpDir)) {
                FileSystem.SYSTEM.createDirectory(tmpDir,mustCreate = true)
            }
        }
    }
    fun getConfigDir(): Path {
        return configDir
    }
    actual fun getTempDir(): Path {
        return tmpDir
    }

    actual fun getDownloadDir(): Path {
        return tmpDir
    }


}