package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties

@Composable
fun AppDialog(
    title: String,
    text: String = "",
    onConfirm: () -> Unit = {},
    onCancel: (() -> Unit)? = null,
    openState: MutableState<Boolean>,
    content: @Composable () -> Unit = {},
) {
    if (openState.value) {
        AlertDialog(
            onDismissRequest = {openState.value = false},
            dismissButton = {
                if (onCancel != null) {
                    TextButton(onClick = {
                        openState.value = false
                        onCancel()
                    }) {
                        Text(text = "取消")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { openState.value = false;onConfirm() }) {
                    Text(text = "确定")
                }
            },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = title)
                }
            },
            text = {
                if (text.isNotEmpty()) {
                    Text(text = text)
                }else {
                    content()
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            )
        )
    }
}