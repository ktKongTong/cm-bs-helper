package io.ktlab.bshelper.ui.event

import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance

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
                try {
                    coroutineContext.ensureActive()
                    onEvent(event)
                }catch (e:Exception) {
                    publish(GlobalUIEvent.ReportError(e,"global error"))
                }
            }
    }
}