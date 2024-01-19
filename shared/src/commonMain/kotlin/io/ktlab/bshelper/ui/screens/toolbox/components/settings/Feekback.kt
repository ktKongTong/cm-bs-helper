package io.ktlab.bshelper.ui.screens.toolbox.components.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowOutward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.BuildConfig
import io.ktlab.bshelper.ui.composables.OpenInBrowser

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Feedback() {
    val openFeedback = remember { mutableStateOf(false) }
    if (openFeedback.value) {
        OpenInBrowser(BuildConfig.FEEDBACK_URL)
    }
    Row (
        modifier = Modifier
            .padding(16.dp,8.dp)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .combinedClickable {
                openFeedback.value = true
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text("意见反馈", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.weight(1f,false))
        Icon(
            imageVector = Icons.Rounded.ArrowOutward,
            contentDescription = "ArrowOutward",
            modifier = Modifier.padding(16.dp)
        )
    }
}