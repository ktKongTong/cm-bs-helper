package io.ktlab.bshelper.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.material.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.ktlab.bshelper.BuildConfig

@Composable
actual fun WebFeedBack() {
    val context = LocalContext.current
    val urlIntent = remember{Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.FEEDBACK_URL))}
    Button(onClick = { context.startActivity(urlIntent) }) { Text("反馈") }
}