package io.ktlab.bshelper.ui.event

import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlin.coroutines.coroutineContext

open class UIEvent



object EventBus {
    private val _events = MutableSharedFlow<UIEvent>()
    val events = _events.asSharedFlow()

    suspend fun publish(event: UIEvent) {
        _events.emit(event)
    }

    suspend inline fun <reified T> subscribe(crossinline onEvent: (T) -> Unit) {
        events.filterIsInstance<T>()
            .collectLatest { event ->
                coroutineContext.ensureActive()
                onEvent(event)
            }
    }
}