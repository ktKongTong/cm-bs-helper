package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.vo.BSMapVO
import io.ktlab.bshelper.model.vo.FSMapVO
import io.ktlab.bshelper.ui.components.labels.*

@Composable
fun MapAvatar(
    source: String,
    modifier: Modifier = Modifier,
    curated: Boolean = false,
    onClick: () -> Unit = {},
) {
    Box(
        modifier =
            modifier
                .clip(shape = MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surface),
    ) {
        AsyncImageWithFallback(
            modifier =
                Modifier
                    .size(152.dp, 152.dp)
                    .clickable { onClick() },
            source = source,
        )
        if (curated) {
            Box(
                modifier =
                    Modifier
                        .size(24.dp, 24.dp)
                        .clip(shape = RoundedCornerShape(4.dp))
                        .align(Alignment.BottomEnd)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
            ) {
                Text(
                    text = "C",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun MapItem(
    map: IMap,
    modifier: Modifier = Modifier,
    onAvatarClick: ((IMap) -> Unit)? = null,
    onLongClick: (IMap) -> Unit = {},
    onClick: (IMap) -> Unit = {},
    onAuthorClick: (IMap) -> Unit = {},
    menuArea: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier =
            modifier
                .clip(shape = MaterialTheme.shapes.medium)
                .fillMaxWidth(),
    ) {
        Row(
            modifier =
                Modifier
                    .combinedClickable(onLongClick = { onLongClick(map) }, onClick = { onClick(map) })
                    .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            MapAvatar(
                source = map.getAvatar(),
                curated =
                    when (map) {
                        is FSMapVO -> map.bsMapWithUploader?.curator != null
                        is BSMapVO -> map.curator != null
                        else -> false
                    },
                onClick = { onAvatarClick?.let { it(map) } },
            )
            Column(
                modifier =
                    Modifier
                        .padding(start = 8.dp, end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                SongNameLabel(songName = map.getSongName())
                FlowRow {
                    MapperLabel(
                        mapperName = map.getAuthor(),
                        onClick = {},
                        verified =
                            when (map) {
                                is FSMapVO -> map.isVerified()
                                is BSMapVO -> map.uploader.verifiedMapper ?: false
                                else -> false
                            },
                        avatarUrl = map.getAuthorAvatar(),
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
                        modifier =
                            Modifier
                                .fillMaxWidth(),
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
                    modifier = Modifier.fillMaxWidth(),
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
            }
        }

        Box(
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .padding(8.dp),
        ) {
            menuArea()
        }
    }
}
