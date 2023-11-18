package io.ktlab.bshelper.ui.screens.toolbox.components


import androidx.compose.runtime.Composable

@Composable
expect fun BSDirectoryPicker(
    show: Boolean,
    initialDirectory: String? = null,
    onFileSelected: (String?) -> Unit
)