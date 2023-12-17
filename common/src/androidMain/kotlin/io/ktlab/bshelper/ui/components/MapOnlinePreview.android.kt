package io.ktlab.bshelper.ui.components

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@SuppressLint("SetJavaScriptEnabled")
@Composable
actual fun MapOnlinePreview(
    onDismiss: () -> Unit,
    mapId: String
)
{
    val context = LocalContext.current
    val webView by remember(LocalContext.current) { mutableStateOf(WebView(context)) }
    webView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null)
    Dialog(

        onDismissRequest = {
            onDismiss()
            webView.destroy()
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        val mUrl = "https://allpoland.github.io/ArcViewer/?id=$mapId"
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    webView.apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        loadUrl(mUrl)
                    }
                }, update = {
                    it.loadUrl(mUrl)
                })
        }
    }
}
