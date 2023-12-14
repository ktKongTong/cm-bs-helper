package io.ktlab.bshelper.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

// format to human friendly style
// like: 1 year ago, 1 month ago, 1 day ago, 1 hour ago, 1 minute ago, 1 second ago
fun LocalDateTime.prettyFormat(): String {
    val now = Clock.System.now()
    val diff = now.epochSeconds - this.toInstant(TimeZone.UTC).epochSeconds
    return when {
        diff > 365 * 24 * 60 * 60 -> "${diff / (365 * 24 * 60 * 60)} years ago"
        diff > 30 * 24 * 60 * 60 -> "${diff / (30 * 24 * 60 * 60)} months ago"
        diff > 24 * 60 * 60 -> "${diff / (24 * 60 * 60)} days ago"
        diff > 60 * 60 -> "${diff / (60 * 60)} hours ago"
        diff > 60 -> "${diff / 60} minutes ago"
        else -> "${diff} seconds ago"
    }
}

fun Long.countPrettyFormat(): String {
    // 14737 -> 1.47w
    // 1473 -> 1.47k
    // 147 -> 0.1k
    // 14 -> 14
    // 1 -> 1
    return when {
        this > 10000 -> "${this / 10000}.${this % 10000 / 1000}w"
        this > 1000 -> "${this / 1000}.${this % 1000 / 100}k"
        this > 100 -> "0.${this / 100}k"
        else -> "$this"
    }
}

fun String.asValidFileName(): String {
    val illegalChars = Regex("[/;\\\\:*?\"<>|\\s]")
    val replacement = "_"
    val result = replace(illegalChars, replacement)
    return result
}