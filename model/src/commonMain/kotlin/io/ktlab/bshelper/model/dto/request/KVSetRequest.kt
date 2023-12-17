package io.ktlab.bshelper.model.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class KVSetRequest<T : @Serializable Any>(
    val value: T,
    val timeout: Int? = null,
)
