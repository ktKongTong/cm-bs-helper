package io.ktlab.bshelper.model.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class KVSetResponse(
    val message: String,
//    val status: KVStatus?,
    val key: String? = null,
    val timeout: Int? = null,
)

@Serializable
data class KVGetResponse<T : @Serializable Any>(
    val message: String,
    val content: T? = null,
)

enum class KVStatus(val value: String) {
    OK("OK"),
    NOT_FOUND("NOT_FOUND"),
    ERROR("ERROR"),
}
