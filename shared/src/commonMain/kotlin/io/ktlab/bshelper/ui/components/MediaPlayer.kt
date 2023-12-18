package io.ktlab.bshelper.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.painterResource
import io.ktlab.bshelper.MR
import io.ktlab.bshelper.ui.viewmodel.CurrentMediaState
import io.ktlab.bshelper.ui.viewmodel.GlobalUIEvent
import io.ktlab.bshelper.ui.viewmodel.IMedia
import io.ktlab.bshelper.ui.viewmodel.MediaEvent

@Composable
fun MediaPlayer(
    media: IMedia,
    currentMediaState: CurrentMediaState,
    onUIEvent: (GlobalUIEvent) -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = 360F,
        animationSpec =
            infiniteRepeatable(
                animation = tween(4000, easing = LinearEasing),
            ),
    )
    val angleState = if (currentMediaState == CurrentMediaState.Playing) angle else 0F
    val clickEvent = {
        when (currentMediaState) {
            CurrentMediaState.Playing -> {
                onUIEvent(GlobalUIEvent.OnMediaEvent(MediaEvent.Pause))
            }
            CurrentMediaState.Paused -> {
                onUIEvent(GlobalUIEvent.OnMediaEvent(MediaEvent.Play))
            }
            else -> {}
        }
    }
    Box(
        modifier =
            Modifier
                .padding(8.dp)
                .clip(shape = CircleShape)
                .size(48.dp),
    ) {
        AnimatedVisibility(visible = media != IMedia.None) {
            when (media) {
                is IMedia.MapAudioPreview -> {
                    AsyncImageWithFallback(
                        modifier =
                            Modifier
                                .clickable { clickEvent() }
                                .rotate(angleState),
                        source = media.avatarUrl ?: "",
                        fallback = {
                            Image(
                                painter = painterResource(MR.images.bs_icon),
                                contentDescription = "default image",
                                modifier =
                                    Modifier
                                        .size(48.dp)
                                        .clip(CircleShape),
                            )
                        },
                    )
                }
                is IMedia.MapPreview -> {
                    // a map preview component
                }
                is IMedia.None -> {
                    Image(
                        painter = painterResource(MR.images.bs_icon),
                        contentDescription = "default image",
                        modifier = Modifier,
                    )
                }
            }
        }
    }
}
