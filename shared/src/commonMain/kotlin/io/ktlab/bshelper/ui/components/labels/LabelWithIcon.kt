package io.ktlab.bshelper.ui.components.labels

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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
    tint: Color = LocalContentColor.current,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp),
    ) {
        BSBPMIcon(modifier, tint)
        Text(
            text = bpm,
            modifier = Modifier.padding(start = 2.dp),
            style = textStyle,
        )
    }
}

@Composable
fun BSDurationLabel(
    duration: String,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp),
    ) {
        BSDurationIcon(modifier, tint)
        Text(
            text = duration,
            modifier = Modifier.padding(start = 2.dp),
            style = textStyle,
        )
    }
}
@Composable
fun BSIDLabel(
    id: String,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp),
    ) {
        BSIDIcon(modifier, tint)
        Text(
            text = id,
            modifier = Modifier.padding(start = 2.dp),
            color = tint,
            style = textStyle,
        )
    }
}
@Composable
fun BSNPSRangeLabel(
    npsRange: String,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp),
    ) {
        BSNPSIcon(modifier, tint)
        Text(
            text = npsRange,
            modifier = Modifier.padding(start = 2.dp),
            style = textStyle,
        )
    }
}

@Composable
fun BSNPSLabel(
    nps: Double,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp),
    ) {
        BSNPSIcon(modifier, tint)
        Text(
            text = nps.fixedStr(2),
            modifier = Modifier.padding(start = 2.dp),
            style = textStyle,
        )
    }
}
@Composable
fun BSNJSLabel(
    njs: Double,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp),
    ) {
        BSNJSIcon(modifier, tint)
        Text(
            text = njs.fixedStr(2),
            modifier = Modifier.padding(start = 2.dp),
            style = textStyle,
        )
    }
}
@Composable
fun BSLightEventLabel(
    event: Long,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp),
    ) {
        BSLightEventIcon(modifier, tint)
        Text(
            text = event.toString(),
            modifier = Modifier.padding(start = 2.dp),
            style = textStyle,
        )
    }
}

@Composable
fun BSNoteLabel(
    note: Long,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp),
    ) {
        BSNoteIcon(modifier, tint)
        Text(
            text = note.toString(),
            modifier = Modifier.padding(start = 2.dp),
            style = textStyle,
        )
    }
}

@Composable
fun BSObstacleLabel(
    obstacle: Long,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp),
    ) {
        BSObstacleIcon(modifier, tint)
        Text(
            text = obstacle.toString(),
            modifier = Modifier.padding(start = 2.dp),
            style = textStyle,
        )
    }
}

@Composable
fun BSBombLabel(
    bomb: Long,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp),
    ) {
        BSBombIcon(modifier, tint)
        Text(
            text = bomb.toString(),
            modifier = Modifier.padding(start = 2.dp),
            style = textStyle,
        )
    }
}

@Composable
fun BSRatingLabel(
    rating: Double,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp),
    ) {
        BSRatingIcon(modifier, tint)
        Text(
            text = (rating * 100).fixedStr(0) + "%",
            modifier = Modifier.padding(start = 2.dp),
            style = textStyle,
        )
    }
}

@Composable
fun BSThumbUpLabel(
    upVote: Long,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp),
    ) {
        BSThumbUpIcon(modifier, tint)
        Text(
            text = upVote.countPrettyFormat(),
            modifier = modifier.padding(2.dp),
            style = textStyle,
        )
    }
}

@Composable
fun BSThumbDownLabel(
    downVote: Long,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp),
    ) {
        BSThumbDownIcon(modifier, tint)
        Text(
            text = downVote.countPrettyFormat(),
            modifier = Modifier.padding(start = 2.dp),
            style = textStyle,
        )
    }
}

@Composable
fun DateLabel(
    date: LocalDateTime,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp),
    ) {
        BSDateIcon(modifier, tint)
        Text(
            text = date.prettyFormat(),
            modifier = Modifier.padding(start = 2.dp),
            style = textStyle,
        )
    }
}

@Composable
fun MapAmountLabel(
    count: Int,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 2.dp),
    ) {
        BSMapAmountIcon(modifier, tint)
        Text(
            text = count.toString(),
            modifier = Modifier.padding(start = 2.dp),
            style = textStyle,
        )
    }
}
