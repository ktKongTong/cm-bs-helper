package io.ktlab.bshelper.service

import android.content.ClipData
import android.content.Context
import androidx.compose.ui.text.AnnotatedString

private const val PLAIN_TEXT_LABEL = "plain text"

class BSHelperAndroidClipboardManager constructor(context: Context) : IBSClipBoardManager {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager

    override fun setText(annotatedString: AnnotatedString) {
        clipboardManager.setPrimaryClip(
            ClipData.newPlainText(
                PLAIN_TEXT_LABEL,
                annotatedString.text,
            ),
        )
    }

    override fun getText(): AnnotatedString? {
        return clipboardManager.primaryClip?.let { primaryClip ->
            if (primaryClip.itemCount > 0) {
                primaryClip.getItemAt(0)?.text?.let { AnnotatedString(it.toString()) }
            } else {
                null
            }
        }
    }

    override fun hasText() = clipboardManager.primaryClipDescription?.hasMimeType("text/*") ?: false
}

actual class BSHelperClipboardFactory(private val context: Context) {
    actual fun createClipboardManager(): IBSClipBoardManager = BSHelperAndroidClipboardManager(context)
}
