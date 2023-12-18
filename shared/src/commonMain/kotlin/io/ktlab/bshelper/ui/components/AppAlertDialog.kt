package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties

interface TriggerScope {
    var open: Boolean
    val onTrigger: () -> Unit
}

class AppAlertDialogScope : TriggerScope {
    override var open by mutableStateOf(false)
    override val onTrigger: () -> Unit get() = { open = !open }
}

@Composable
fun rememberDialogTriggerScope(): AppAlertDialogScope {
    return remember { AppAlertDialogScope() }
}

@Composable
fun AppAlertDialog(
    title: String,
    text: String = "",
    onConfirm: () -> Unit = {},
    onCancel: (() -> Unit)? = null,
    triggerBy: @Composable TriggerScope.() -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    val scope = rememberDialogTriggerScope()
    triggerBy(scope)
    if (scope.open) {
        AlertDialog(
            onDismissRequest = { scope.open = false },
            dismissButton = {
                if (onCancel != null) {
                    TextButton(onClick = {
                        scope.open = false
                        onCancel()
                    }) {
                        Text(text = "取消")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.open = false
                    onConfirm()
                }) {
                    Text(text = "确定")
                }
            },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(text = title)
                }
            },
            text = {
                if (text.isNotEmpty()) {
                    Text(text = text)
                } else {
                    content()
                }
            },
            properties =
                DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                ),
        )
    }
}
