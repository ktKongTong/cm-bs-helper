package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.ThumbDown
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun IconWIthText(
    onClick: () -> Unit,
    icon: ImageVector,
    description:String ,text: String,
    tint: Color = LocalContentColor.current
) {
    Row {
        Icon(
            imageVector = icon,
            contentDescription = description,
            modifier = Modifier.size(20.dp),
            tint = tint
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, textAlign = TextAlign.Center, softWrap = false, maxLines = 1)
    }
}

@Composable
fun ThumbUpIconWIthText(
    text: String,
    onClick: () -> Unit = {},
    tint: Color = LocalContentColor.current
) {
    IconWIthText(
        onClick = onClick,
        icon = Icons.Rounded.ThumbUp,
        description = "Thumb Up",
        text = text,
        tint = tint
    )
}
@Composable
fun ThumbDownIconWIthText(
    text: String,
    onClick: () -> Unit = {},
    tint: Color = LocalContentColor.current
) {
    IconWIthText(
        onClick = onClick,
        icon = Icons.Rounded.ThumbDown,
        description = "Thumb Down",
        text = text,
        tint = tint
    )
}

@Composable
fun NPSIconWIthText(onClick: () -> Unit = {}, text: String) {
    IconWIthText(
        onClick = onClick,
        icon = Icons.Filled.Speed,
        description = "NPS",
        text = text)
}

@Composable
fun MapAmountIconWIthText(onClick: () -> Unit = {}, text: String) {
    IconWIthText(
        onClick = onClick,
        icon = Icons.Filled.Map,
        description = "Map Amount",
        text = text)
}

@Composable
fun DurationIconWIthText(onClick: () -> Unit = {}, text: String) {
    IconWIthText(
        onClick = onClick,
        icon = Icons.Filled.AccessTime,
        description = "Duration",
        text = text)
}

@Composable
fun MapperIconWIthText(onClick: () -> Unit = {}, text: String, description: String = "Mapper") {
    IconWIthText(
        onClick = onClick,
        icon = Icons.Filled.Person,
        description = description,
        text = text)
}
@Composable
fun VerifiedMapperIconWIthText(onClick: () -> Unit = {}, text: String, description: String = "Mapper") {
    IconWIthText(
        onClick = onClick,
        icon = Icons.Filled.Person,
        description = description,
        text = text)
}
@Composable
fun BPMIconWIthText(onClick: () -> Unit = {}, text: String) {
    IconWIthText(
        onClick = onClick,
        icon = Icons.Filled.PunchClock,
        description = "BPM",
        text = text)
}

@Composable
fun MapIdIconWIthText(onClick: () -> Unit = {}, text: String) {
    IconWIthText(
        onClick = onClick,
        icon = Icons.Filled.CreditCard,
        description = "BPM",
        text = text)
}