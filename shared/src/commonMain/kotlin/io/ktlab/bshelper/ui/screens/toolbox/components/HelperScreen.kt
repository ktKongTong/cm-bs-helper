package io.ktlab.bshelper.ui.screens.toolbox.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.ktlab.bshelper.platform.currentPlatform

@Composable
fun HelperScreen() {
    val currentPlatform = remember{currentPlatform()}

}