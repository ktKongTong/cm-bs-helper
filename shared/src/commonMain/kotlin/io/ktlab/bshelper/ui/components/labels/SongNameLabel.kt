package io.ktlab.bshelper.ui.components.labels

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun SongNameLabel(
    songName: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = Modifier,
        text = songName,
        style = MaterialTheme.typography.titleLarge,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Ellipsis,
    )
}
