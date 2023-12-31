package io.ktlab.bshelper.platform

import io.github.oshai.kotlinlogging.KotlinLogging
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

private val logger = KotlinLogging.logger {}
actual class StorageService {

    private val homeDir = System.getProperty("user.home") ?: throw IllegalStateException("can't get user home dir")
    private var appDir: Path = homeDir.toPath().resolve(".bshelper")
    private var configDir: Path = appDir.resolve("config")
    private var tmpDir: Path = appDir.resolve("tmp")
    private var backupDir: Path = appDir.resolve("backup")
    init {
        logger.debug { "Init StorageService app dir:${appDir}" }
        synchronized(this) {
            if (!FileSystem.SYSTEM.exists(appDir)) {
                logger.debug { "app dir not exist, create it: $appDir" }
                FileSystem.SYSTEM.createDirectory(appDir, mustCreate = true)
            }
            if (!FileSystem.SYSTEM.exists(configDir)) {
                logger.debug { "app dir not exist, create it: $configDir" }
                FileSystem.SYSTEM.createDirectory(configDir, mustCreate = true)
            }
            if (!FileSystem.SYSTEM.exists(tmpDir)) {
                logger.debug { "tmpDir dir not exist, create it: $tmpDir" }
                FileSystem.SYSTEM.createDirectory(tmpDir, mustCreate = true)
            }
            if (!FileSystem.SYSTEM.exists(backupDir)) {
                logger.debug { "backupDir dir not exist, create it: $backupDir" }
                FileSystem.SYSTEM.createDirectory(backupDir, mustCreate = true)
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

    actual fun getBackupDir(): Path {
        return backupDir
    }

    actual fun getBackupDir(dir: Path): Path {
        return backupDir
    }

}
