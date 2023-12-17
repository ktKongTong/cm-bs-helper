package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import io.ktlab.bshelper.ui.components.TriggerScope

class ExposedDropdownMenuTriggerScope : TriggerScope {
    override var open by mutableStateOf(false)
    override val onTrigger: () -> Unit
        get() = { open = !open }
}

@Composable
fun rememberDropdownMenuTriggerScope(): TriggerScope {
    return remember { ExposedDropdownMenuTriggerScope() }
}

@Composable
fun IconExposedDropDownMenu(
    modifier: Modifier = Modifier,
    content: @Composable TriggerScope.() -> Unit = {},
) {
    val scope = rememberDropdownMenuTriggerScope()
    IconButton(
        modifier = modifier,
        onClick = { scope.open = true },
    ) {
        Icon(Icons.Default.MoreVert, contentDescription = "")
    }
    DropdownMenu(
        expanded = scope.open,
        onDismissRequest = { scope.open = false },
        modifier = Modifier,
    ) {
        content(scope)
    }
}
