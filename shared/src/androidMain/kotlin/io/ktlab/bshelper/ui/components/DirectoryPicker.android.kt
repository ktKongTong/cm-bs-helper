package io.ktlab.bshelper.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.net.toUri

@Composable
actual fun BSDirectoryPicker(
    show: Boolean,
    initialDirectory: String?,
    onFileSelected: (String?) -> Unit,
) {
    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocumentTree(),
        ) { uri ->
            if (uri == null) return@rememberLauncherForActivityResult
            var path = uri.path

            path = path?.replace("/tree/primary:", "/storage/emulated/0/")
                ?.replace("/tree/raw:","")
            onFileSelected(path)
        }
    LaunchedEffect(show) {
        launcher.launch("/storage/emulated/0/".toUri())
    }
}
