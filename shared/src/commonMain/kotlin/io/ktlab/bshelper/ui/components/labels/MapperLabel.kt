package io.ktlab.bshelper.ui.components.labels

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.ui.components.AsyncImageWithFallback

@Composable
fun MapperLabel(
    mapperName: String,
    avatarUrl: String? = null,
    verified: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    Row(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .hoverable(interactionSource = interactionSource),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            if (avatarUrl != null) {
                AsyncImageWithFallback(
                    Modifier
                        .clip(shape = CircleShape)
                        .size(24.dp),
                    source = avatarUrl,
                    fallback = {
                        Icon(
                            Icons.Rounded.Person,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(24.dp),
                        )
                    },
                )
            }
            if (verified) {
                Icon(
                    Icons.Filled.Verified,
                    modifier =
                        Modifier
                            .align(Alignment.BottomEnd)
                            .size(12.dp)
                            .offset(x = 4.dp, y = (4).dp),
                    contentDescription = "Verified Mapper Icon",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = mapperName,
            modifier = Modifier.padding(start = 2.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
