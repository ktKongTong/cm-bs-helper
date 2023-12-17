package io.ktlab.bshelper.ui.components

import androidx.compose.runtime.Composable

@Composable
expect fun MapOnlinePreview(
    onDismiss: () -> Unit,
    mapId: String,
)
