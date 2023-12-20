package io.ktlab.bshelper.utils

import net.lingala.zip4j.ZipFile

fun unzip(
    zipFilePath: String,
    destPath: String,
) {
    ZipFile(zipFilePath).extractAll(destPath)
}