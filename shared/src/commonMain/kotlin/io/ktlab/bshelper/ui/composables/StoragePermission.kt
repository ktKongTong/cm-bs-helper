package io.ktlab.bshelper.ui.composables

import androidx.compose.runtime.Composable


@Composable
expect fun isStoragePermissionGranted(): Boolean

@Composable
expect fun RequestStoragePermission()