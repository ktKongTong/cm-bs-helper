package io.ktlab.bshelper.ui.composables

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun OpenInBrowser(url: String) {
    val context = LocalContext.current
    val urlIntent = remember{ Intent(Intent.ACTION_VIEW, Uri.parse(url)) }
    LaunchedEffect(url) {
        context.startActivity(urlIntent)
    }
}