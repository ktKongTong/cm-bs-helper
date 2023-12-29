package io.ktlab.bshelper.model.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ToolAPIResponse<T>(
    val message: String,
    val data: T? = null,
    val code: Int
)