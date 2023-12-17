package io.ktlab.bshelper.ui.components.chiptextfield.core.util

import kotlinx.coroutines.android.awaitFrame as androidAwaitFrame
actual suspend fun awaitFrame() {
    androidAwaitFrame()
}