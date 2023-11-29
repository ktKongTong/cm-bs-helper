package io.ktlab.bshelper.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.Exception


enum  class EventType(
    private val dataType: Any? = null
) {
    Exception(Exception::class),
    Message(String::class),
}



data class Event(
    val type: EventType,
    val data: Any? = null
)

class RuntimeEventFlow {
    private val eventFlow = MutableStateFlow<Event?>(null)


    private val scope = CoroutineScope(SupervisorJob())
    init {
        scope.consumeEvent()
    }

    private val listeners = mutableListOf<(Event) -> Unit>()


    private fun CoroutineScope.consumeEvent() {
        launch {
            eventFlow.collect{ event ->
                if (event == null) return@collect
                listeners.forEach {
                    it(event)
                }
            }
        }
    }

    fun sendEvent(event: Event) {
        eventFlow.update {
            event
        }
    }


    fun subscribeEvent(onEvent: (Event) -> Unit) {
        synchronized(this) {
            listeners.add(onEvent)
        }
    }

    fun unsubscribeEvent(onEvent: (Event) -> Unit) {
        synchronized(this) {
            listeners.remove(onEvent)
        }
    }

}