package io.ktlab.bshelper.ui.composables

import androidx.compose.runtime.Composable

@Composable
actual fun RequestStoragePermission() {
}

@Composable
actual fun isStoragePermissionGranted(): Boolean {
    return true
}
