package io.ktlab.bshelper.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun RequestStoragePermission() {
}

@Composable
actual fun isStoragePermissionGranted(): Boolean {
    return remember { true }
}
