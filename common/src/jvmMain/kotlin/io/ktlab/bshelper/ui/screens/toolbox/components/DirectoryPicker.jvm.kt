package io.ktlab.bshelper.ui.screens.toolbox.components

import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker


import androidx.compose.runtime.Composable
@Composable
actual fun BSDirectoryPicker(
    show: Boolean,
    initialDirectory: String?,
    onFileSelected: (String?) -> Unit
) {
    DirectoryPicker(show,initialDirectory,onFileSelected)
}