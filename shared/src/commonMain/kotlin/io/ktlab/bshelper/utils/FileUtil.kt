package io.ktlab.bshelper.utils

import okio.Path.Companion.toPath
import okio.buffer
import okio.source
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

fun String.asValidFilename(filename: String): String {
    return filename.replace("[\\\\/:*?\"<>|]".toRegex(), "_")
}

@OptIn(ExperimentalEncodingApi::class)
fun encodeImageToBase64(imagePath: String): String {
    imagePath.toPath().toFile().source().buffer().use {
        return Base64.encode(it.readByteArray())
    }
}

