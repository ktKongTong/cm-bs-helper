package io.ktlab.bshelper.platform

import androidx.compose.ui.text.AnnotatedString

interface IBSClipBoardManager {
    fun setText(annotatedString: AnnotatedString)

    fun getText(): AnnotatedString?

    fun hasText(): Boolean
}

expect class BSHelperClipboardFactory {
    fun createClipboardManager(): IBSClipBoardManager
}

fun createClipboardManager(bSHelperClipboardFactory: BSHelperClipboardFactory): IBSClipBoardManager {
    return bSHelperClipboardFactory.createClipboardManager()
}
