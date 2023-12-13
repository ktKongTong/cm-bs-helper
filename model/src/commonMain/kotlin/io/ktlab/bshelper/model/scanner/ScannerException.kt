package io.ktlab.bshelper.model.scanner

import okio.Path


//open class ScannerException(
//    override val message: String,
//    override val cause: Throwable? = null,
//): Exception(message, cause)

sealed interface ScannerException {

    class JSONFileTooLargeException(
        override val message: String,
        override val cause: Throwable? = null,
        val mapId: String? = null,
        val mapPath: Path,
    ): ScannerException, Exception(message, cause)

    // 文件缺失异常
// 1. Info.dat/info.dat 不存在
// 2. Info.dat/info.dat 无法解析
// 3. Info.dat/info.dat 指明的文件不存在
    class FileMissingException(
        override val message: String,
        override val cause: Throwable? = null,
        val lackInfo: Boolean = false,
        val mapId: String? = null,
        val mapDir: String? = null,
    ): ScannerException, Exception(message, cause)
    class ParseException(
        override val message: String,
        override val cause: Throwable? = null,
        val lackInfo: Boolean = false,
        val mapId: String? = null,
        val mapDir: String? = null,
    ): ScannerException, Exception(message, cause)
}
