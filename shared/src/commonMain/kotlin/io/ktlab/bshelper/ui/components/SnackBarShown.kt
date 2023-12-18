package io.ktlab.bshelper.ui.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import io.ktlab.bshelper.ui.event.SnackBarMessage

@Composable
fun SnackBarShown(
    snackbarHostState: SnackbarHostState,
    snackBarMessages: List<SnackBarMessage>,
    onSnackBarShown: (Long) -> Unit,
) {
    if (snackBarMessages.isNotEmpty()) {
        val snackBarMessage = remember { snackBarMessages[0] }
        val msg: String = snackBarMessage.message
        val actionLabel = snackBarMessage.actionLabel

        val onSnackBarShownState by rememberUpdatedState(onSnackBarShown)
        LaunchedEffect(msg, actionLabel, snackbarHostState) {
            val snackbarResult =
                snackbarHostState.showSnackbar(
                    message = msg,
                    actionLabel = actionLabel,
                    duration = snackBarMessage.duration,
                )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                if (snackBarMessage.action != null) {
                    snackBarMessage.action?.let { it() }
                }
            }
            onSnackBarShownState(snackBarMessage.id)
        }
    }
}
