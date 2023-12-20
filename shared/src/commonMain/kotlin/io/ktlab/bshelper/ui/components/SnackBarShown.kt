package io.ktlab.bshelper.ui.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktlab.bshelper.ui.viewmodel.SnackBarMessage

private val logger = KotlinLogging.logger {}
@Composable
fun SnackBarShown(
    snackbarHostState: SnackbarHostState,
    snackBarMessages: List<SnackBarMessage>,
    onSnackBarShown: (Long) -> Unit,
) {
    if (snackBarMessages.isNotEmpty()) {
        val snackBarMessage = snackBarMessages.first()
        val msg: String = snackBarMessage.message
        val actionLabel = snackBarMessage.actionLabel
        logger.debug { "SnackBarShown: $msg" }
        val onSnackBarShownState by rememberUpdatedState(onSnackBarShown)

        LaunchedEffect(msg, actionLabel, snackbarHostState) {
            logger.debug { "SnackBarShown: LaunchedEffect" }
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
