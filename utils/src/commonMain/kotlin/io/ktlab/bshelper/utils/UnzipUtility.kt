package io.ktlab.bshelper.utils

import net.lingala.zip4j.ZipFile

object UnzipUtility {
    fun unzip(
        zipFilePath: String,
        destDir: String,
    ) {
        ZipFile(zipFilePath).extractAll(destDir)
    }
}
