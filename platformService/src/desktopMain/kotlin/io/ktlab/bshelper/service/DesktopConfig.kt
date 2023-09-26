package io.ktlab.bshelper.service

import java.nio.file.Path
import kotlin.io.path.Path

object DesktopConfig {
    private val configPath = Path(System.getProperty("user.home"), ".config/cm-bs-helper")
    init {
        if (!configPath.toFile().exists() && !configPath.toFile().mkdirs()) {
            throw Exception("can't create config path")
        }
    }

     val DB_URL: String = "jdbc:sqlite:$configPath/bs-helper.db"
          get() {
              createDBFileIfNotExist()
              return field
          }

    private fun createDBFileIfNotExist() {
        val dbFile = configPath.resolve("bs-helper.db")
        if (!dbFile.toFile().exists()) {
            dbFile.toFile().createNewFile()
        }
    }

    fun getConfigPath(): Path = configPath
}