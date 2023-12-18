package io.ktlab.bshelper.platform

import androidx.compose.ui.text.AnnotatedString

class BSHelperDesktopClipboardManager : IBSClipBoardManager {
    private val skikoClipboardManager = org.jetbrains.skiko.ClipboardManager()

    override fun getText(): AnnotatedString? = skikoClipboardManager.getText()?.let { AnnotatedString(it) }

    override fun setText(annotatedString: AnnotatedString) {
        skikoClipboardManager.setText(annotatedString.text)
    }

    override fun hasText(): Boolean = skikoClipboardManager.hasText()
}

actual class BSHelperClipboardFactory {
    actual fun createClipboardManager(): IBSClipBoardManager = BSHelperDesktopClipboardManager()
}
