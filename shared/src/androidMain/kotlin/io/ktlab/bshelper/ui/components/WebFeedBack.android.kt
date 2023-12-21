package io.ktlab.bshelper.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.ktlab.bshelper.BuildConfig

@Composable
actual fun WebFeedBack(open: Boolean) {
    var triggerTime by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val urlIntent = remember{Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.FEEDBACK_URL))}
    if(open) {
        if (triggerTime == 0) {
            context.startActivity(urlIntent)
        }
        triggerTime += 1
    }

}