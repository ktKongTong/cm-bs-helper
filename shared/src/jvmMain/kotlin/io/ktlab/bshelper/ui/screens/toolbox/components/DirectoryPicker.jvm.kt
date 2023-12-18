package io.ktlab.bshelper.ui.screens.toolbox.components

import androidx.compose.runtime.Composable
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker

@Composable
actual fun BSDirectoryPicker(
    show: Boolean,
    initialDirectory: String?,
    onFileSelected: (String?) -> Unit,
) {
    DirectoryPicker(show, initialDirectory, onFileSelected)
}
