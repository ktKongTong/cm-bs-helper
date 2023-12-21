package io.ktlab.bshelper.model

import kotlinx.serialization.Serializable

@Serializable
data class AppVersionChangeLog(
    val version: String,
    val date: String,
    val releaseNote: String,
    val url: String,
)