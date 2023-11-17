package io.ktlab.kown

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import java.security.MessageDigest
import kotlin.experimental.and


fun getPath(dirPath: String, fileName: String): Path {
    return dirPath.toPath().resolve(fileName)
}
fun getTempPath(dirPath: String, filename: String): Path {
    return getPath(dirPath, "$filename.kown")
}

fun checkIfExistAndDelete(path: Path) {
    if (FileSystem.SYSTEM.exists(path)) {
        FileSystem.SYSTEM.delete(path)
    }
}

fun getUniqueId(url: String, dirPath: String, filename: String): String {

    val string = url + Path.DIRECTORY_SEPARATOR + dirPath + Path.DIRECTORY_SEPARATOR + filename
    val hash: ByteArray = MessageDigest.getInstance("MD5")
        .digest(string.toByteArray(charset("UTF-8")))

    val hex = StringBuilder(hash.size * 2)
    for (b in hash) {
        if (b and 0xFF.toByte() < 0x10) hex.append("0")
        hex.append(Integer.toHexString((b and 0xFF.toByte()).toInt()))
    }
    return hex.hashCode().toString()
}

inline val Int.B: Int get() = this
inline val Int.KB: Int get() = this * 1024
inline val Int.MB: Int get() = this * 1024 * 1024
inline val Int.GB: Int get() = this * 1024 * 1024 * 1024
inline val Long.B: Long get() = this
inline val Long.KB: Long get() = this * 1024
inline val Long.MB: Long get() = this * 1024 * 1024
inline val Long.GB: Long get() = this * 1024 * 1024 * 1024

inline val Long.MILLIS: Long get() = this
inline val Long.SECONDS: Long get() = this * 1000
inline val Long.MINUTES: Long get() = this * 1000 * 60
inline val Long.HOURS: Long get() = this * 1000 * 60 * 60
inline val Long.DAYS: Long get() = this * 1000 * 60 * 60 * 24



object Constants {
    const val DEFAULT_USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0"
    val DEFAULT_READ_TIMEOUT = 20000L.MILLIS
    val DEFAULT_CONNECT_TIMEOUT = 20000L.MILLIS
    object DEFAULT {
        val READ_TIMEOUT = 20000L.MILLIS
        val CONNECT_TIMEOUT = 20000L.MILLIS
        val BUFFER_SIZE = 8192.B
    }

}