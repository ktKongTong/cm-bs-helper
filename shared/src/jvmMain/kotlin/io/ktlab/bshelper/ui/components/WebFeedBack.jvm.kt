package io.ktlab.bshelper.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.ktlab.bshelper.BuildConfig

@Composable
actual fun WebFeedBack(open: Boolean) {
    var triggerTime by remember { mutableIntStateOf(0) }
    if(open) {
        if (triggerTime == 0) {
            openInBrowser(BuildConfig.FEEDBACK_URL)
        }
        triggerTime += 1
    }
}