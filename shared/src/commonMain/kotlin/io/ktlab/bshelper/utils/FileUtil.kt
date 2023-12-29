package io.ktlab.bshelper.utils

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.source

fun String.asValidFilename(): String  = this.replace(Regex("[\\\\/:*?\"<>|]"), "_")

fun String.isValidFilename(): Boolean {
    return this.matches(Regex("[^\\\\/:*?\"<>|]+"))
}

@OptIn(ExperimentalEncodingApi::class)
fun encodeImageToBase64(imagePath: String): String {
    imagePath.toPath().toFile().source().buffer().use {
        return Base64.encode(it.readByteArray())
    }
}

// if dir exist add (1), (2), (3), ... to the end of the directory name
fun newDirEvenIfDirExist(path: Path) : Path {
    var dir = path
    var i = 1
    while (FileSystem.SYSTEM.exists(dir)) {
        dir.parent?.let {
            dir = it.resolve("${dir.name} (${i++})")
        }
    }
    FileSystem.SYSTEM.createDirectory(dir)
    return dir
}

fun checkDirExist(path: String): Boolean {
    val dir = path.toPath().toFile()
    return dir.exists()
}