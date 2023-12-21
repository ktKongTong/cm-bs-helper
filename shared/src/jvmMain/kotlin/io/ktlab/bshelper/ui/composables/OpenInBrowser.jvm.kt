package io.ktlab.bshelper.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import java.awt.Desktop
import java.net.URI
import java.util.*

@Composable
actual fun OpenInBrowser(url: String) {
    LaunchedEffect(url) {
        openInBrowser(url)
    }
}

private fun openInBrowser(uri: String) {
    val osName by lazy(LazyThreadSafetyMode.NONE) { System.getProperty("os.name").lowercase(Locale.getDefault()) }
    val desktop = Desktop.getDesktop()
    when {
        Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE) -> desktop.browse(URI(uri))
        "mac" in osName -> Runtime.getRuntime().exec("open $uri")
        "nix" in osName || "nux" in osName -> Runtime.getRuntime().exec("xdg-open $uri")
        else -> throw RuntimeException("cannot open $uri")
    }
}