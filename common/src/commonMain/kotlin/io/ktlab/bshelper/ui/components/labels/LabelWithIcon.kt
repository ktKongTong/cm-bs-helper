package io.ktlab.bshelper.ui.components.labels

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.beatmaps.common.fixedStr
import io.ktlab.bshelper.ui.components.icons.*
import io.ktlab.bshelper.utils.countPrettyFormat
import io.ktlab.bshelper.utils.prettyFormat
import kotlinx.datetime.LocalDateTime

@Composable
fun BSBPMLabel(
    bpm: String,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp)
    ) {
        BSDurationIcon(modifier, tint)
        Text(
            text = bpm,
            modifier = Modifier.padding(start = 2.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun BSDurationLabel(
    duration: String,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp)
    ) {
        BSDurationIcon(modifier, tint)
        Text(
            text =duration,
            modifier = Modifier.padding(start = 2.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun BSNPSRangeLabel(
    npsRange: String,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp)
    ) {
        BSNPSIcon(modifier, tint)
        Text(
            text = npsRange,
            modifier = Modifier.padding(start = 2.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun BSNPSLabel(
    nps: Double,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp)
    ) {
        BSNPSIcon(modifier, tint)
        Text(
            text = String.format("%.2f", nps),
            modifier = Modifier.padding(start = 2.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun BSLightEventLabel(
    event: Long,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp)
    ) {
        BSLightEventIcon(modifier, tint)
        Text(
            text = event.toString(),
            modifier = Modifier.padding(start = 2.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun BSNoteLabel(
    note: Long,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp)
    ) {
        BSNoteIcon(modifier, tint)
        Text(
            text = note.toString(),
            modifier = Modifier.padding(start = 2.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun BSObstacleLabel(
    obstacle: Long,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp)
    ) {
        BSObstacleIcon(modifier, tint)
        Text(
            text = obstacle.toString(),
            modifier = Modifier.padding(start = 2.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun BSBombLabel(
    bomb: Long,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp)
    ) {
        BSBombIcon(modifier, tint)
        Text(
            text = bomb.toString(),
            modifier = Modifier.padding(start = 2.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}


@Composable
fun BSRatingLabel(
    rating: Double,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp)
    ) {
        BSRatingIcon(modifier, tint)
        Text(
            text = (rating * 100).fixedStr(0)+"%",
            modifier = Modifier.padding(start = 2.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun BSThumbUpLabel(
    upVote: Long,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp)
    ) {
        BSThumbUpIcon(modifier, tint)
        Text(
            text = upVote.countPrettyFormat(),
            modifier = Modifier.padding(start = 2.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun BSThumbDownLabel(
    downVote: Long,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp)
    ) {
        BSThumbDownIcon(modifier, tint)
        Text(
            text = downVote.countPrettyFormat(),
            modifier = Modifier.padding(start = 2.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun DateLabel(
    date: LocalDateTime,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp)
    ) {
        BSDateIcon(modifier, tint)
        Text(
            text = date.prettyFormat(),
            modifier = Modifier.padding(start = 2.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}