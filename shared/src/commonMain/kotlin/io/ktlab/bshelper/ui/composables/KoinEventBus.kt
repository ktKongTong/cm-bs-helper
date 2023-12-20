package io.ktlab.bshelper.ui.composables

import androidx.compose.runtime.Composable
import io.ktlab.bshelper.ui.event.EventBus
import moe.tlaster.precompose.stateholder.LocalStateHolder
import moe.tlaster.precompose.stateholder.StateHolder
import org.koin.compose.LocalKoinScope
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

//@Composable
//inline fun koinEventBus(
//    qualifier: Qualifier? = null,
//    stateHolder: StateHolder = checkNotNull(LocalStateHolder.current) {
//        "No StateHolder was provided via LocalStateHolder"
//    },
//    key: String? = null,
//    scope: Scope = LocalKoinScope.current,
//    noinline parameters: ParametersDefinition? = null,
//): EventBus {
//    return stateHolder.getOrPut(qualifier?.value ?: key ?: EventBus::class.simpleName ?: "") {
//        scope.get(EventBus::class, qualifier, parameters)
//    }
//}
