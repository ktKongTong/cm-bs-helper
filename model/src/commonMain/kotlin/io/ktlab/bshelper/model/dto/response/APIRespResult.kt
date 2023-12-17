package io.ktlab.bshelper.model.dto.response

import kotlinx.serialization.Serializable

@Serializable
sealed class APIRespResult<out R> {
    data class Success<out T>(val data: T) : APIRespResult<T>()

    data class Error(val exception: Exception) : APIRespResult<Nothing>()
}

fun <T> APIRespResult<T>.isSuccess(): Boolean {
    return this is APIRespResult.Success<T>
}

fun <T> APIRespResult<T>.successOr(fallback: T): T {
    return (this as? APIRespResult.Success<T>)?.data ?: fallback
}

fun <T> APIRespResult<T>.errorMsg(): Exception {
    return (this as? APIRespResult.Error)?.exception ?: Exception("Unknown error")
}
