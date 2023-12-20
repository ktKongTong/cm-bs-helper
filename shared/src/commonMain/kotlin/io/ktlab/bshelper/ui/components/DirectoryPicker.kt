package io.ktlab.bshelper.ui.components

import androidx.compose.runtime.Composable

@Composable
expect fun BSDirectoryPicker(
    show: Boolean,
    initialDirectory: String? = null,
    onFileSelected: (String?) -> Unit,
)
