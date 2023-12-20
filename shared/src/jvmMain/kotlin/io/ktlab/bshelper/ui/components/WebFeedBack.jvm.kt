package io.ktlab.bshelper.ui.components

import androidx.compose.material.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import io.ktlab.bshelper.BuildConfig

@Composable
actual fun WebFeedBack() {
    Button(onClick = { openInBrowser(BuildConfig.FEEDBACK_URL) }) { Text("反馈") }
}