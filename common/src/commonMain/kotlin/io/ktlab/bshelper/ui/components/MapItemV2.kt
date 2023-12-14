package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.vo.BSMapVO
import io.ktlab.bshelper.model.vo.FSMapVO
import io.ktlab.bshelper.ui.components.labels.*
import io.ktlab.bshelper.ui.screens.beatsaver.components.BSMapFeatureLabel


@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun MapItemV2(
    map: IMap,
    modifier: Modifier = Modifier,
    onAvatarClick: (IMap) -> Unit = {},
    onLongClick: (IMap) -> Unit = {},
    onClick: (IMap) -> Unit = {},
    onAuthorClick: (IMap) -> Unit = {},
    menuArea: @Composable BoxScope.() -> Unit = {},
) {
    Box (
        modifier = modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .combinedClickable(onLongClick = {onLongClick(map)}, onClick = {onClick(map)})
            .fillMaxWidth()
//            .height(IntrinsicSize.Max)

    ) {
        Box (
            Modifier
                .height(IntrinsicSize.Min)
        ){

        Box(
            Modifier
                .align(Alignment.CenterEnd)
                .widthIn(max = 200.dp)
                .fillMaxHeight()
        ) {
            AsyncImageWithFallback(
                source = map.getAvatar(),
                modifier = Modifier
                    .clip(shape = MaterialTheme.shapes.medium),
                contentScale = ContentScale.FillWidth,
                alpha = 0.9f
            )
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
            ) {
                val gradient = Brush.horizontalGradient(
                    0f to Color.White,
                    0.8f to Color.Transparent,
                )
                drawRect(brush = gradient)
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SongNameLabel(songName = map.getSongName())
            FlowRow {
                MapperLabel(
                    mapperName = map.getAuthor(),
                    onClick = {},
                    verified = when (map) {
                        is FSMapVO -> map.isVerified()
                        is BSMapVO -> map.uploader.verifiedMapper ?: false
                        else -> false
                    },
                    avatarUrl = map.getAvatar()
                )
                when (map) {
                    is FSMapVO -> {
                        map.bsMapWithUploader?.bsMap?.createdAt?.let {
                            DateLabel(date = it)
                        }
                    }

                    is BSMapVO -> {
                        DateLabel(date = map.map.createdAt)
                    }
                }
            }
            if (map.isRelateWithBSMap()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    when (map) {
                        is FSMapVO -> {
                            BSThumbUpLabel(map.bsMapWithUploader?.bsMap?.upVotes ?: 0)
                            BSThumbDownLabel(map.bsMapWithUploader?.bsMap?.downVotes ?: 0)
                            BSRatingLabel(map.bsMapWithUploader?.bsMap?.score ?: 0.0)
                        }

                        is BSMapVO -> {
                            BSThumbUpLabel(map.map.upVotes)
                            BSThumbDownLabel(map.map.downVotes)
                            BSRatingLabel(map.map.score)
                        }
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                BSNPSLabel(nps = map.getMaxNPS())
                BSDurationLabel(duration = map.getDuration())
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                BSMapFeatureLabel(map = map)
            }
            when (map) {
                is FSMapVO -> {
                    MapTags(tags = map.bsMapWithUploader?.bsMap?.tags ?: listOf())
                }

                is BSMapVO -> {
                    MapTags(tags = map.map.tags)
                }
            }
            Box(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                menuArea()
            }
        }
        }
    }

}