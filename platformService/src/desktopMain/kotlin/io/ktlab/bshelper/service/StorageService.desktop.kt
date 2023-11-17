package io.ktlab.bshelper.service

import java.io.File

actual class StorageService {

//    private
    //

    private var appDir : File
    private var configDir: File
    private var tmpDir: File
    init {
        val homeDir = System.getProperty("user.home") ?: throw IllegalStateException("can't get user home dir")
        appDir = File(homeDir, ".bshelper")
        configDir = File(appDir, "config")
        tmpDir = appDir.toPath().resolve("tmp").toFile()
        synchronized(this){
            if (!appDir.exists()) {
                appDir.mkdirs()
            }
            if (!configDir.exists()) {
                configDir.mkdirs()
            }
            if (!tmpDir.exists()) {
                tmpDir.mkdirs()
            }
        }
    }

    fun getConfigDir(): File {
        return configDir
    }
    fun getTempDir(): File {
        return tmpDir
    }

    actual fun getDownloadDir(): File {
        return tmpDir
    }


}