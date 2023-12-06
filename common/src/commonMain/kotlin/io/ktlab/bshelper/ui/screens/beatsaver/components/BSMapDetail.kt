package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.HeartBroken
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.dto.response.BSMapReviewDTO
import io.ktlab.bshelper.model.dto.response.BSMapReviewSentiment
import io.ktlab.bshelper.model.vo.BSMapVO
import io.ktlab.bshelper.ui.components.AsyncImageWithFallback
import io.ktlab.bshelper.ui.components.MapTags
import io.ktlab.bshelper.ui.components.MapperLabel
import io.ktlab.bshelper.ui.components.ResizeTwoColumnHeightRow
import io.ktlab.bshelper.ui.components.labels.*
import io.ktlab.bshelper.utils.prettyFormat
import io.ktlab.bshelper.viewmodel.BeatSaverUIEvent

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BSMapDetail(
    map: IMap,
    comments: List<BSMapReviewDTO>,
    onUIEvent: (BeatSaverUIEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column (modifier = modifier.fillMaxSize()) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                onClick = { onUIEvent(BeatSaverUIEvent.OnExitSelectBSMap) },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(text = "Back")
            }
            Text(
                text = map.getSongName(),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }
        ResizeTwoColumnHeightRow {
                Column(
                    modifier = Modifier
                        .widthIn(max = 400.dp)
                        .padding(horizontal = 16.dp),
                ) {
                    Row {
                        AsyncImageWithFallback(
                            modifier = Modifier
                                .padding(PaddingValues(start = 0.dp, top = 8.dp, end = 8.dp, bottom = 8.dp))
                                .size(128.dp, 128.dp)
                                .align(Alignment.Top)
                                .clip(shape = RoundedCornerShape(10.dp)),
                            source = map.getAvatar(),
                        )
                        Column(
                            modifier = Modifier
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                MapperLabel(
                                    mapperName = map.getAuthor(),
                                    onClick = {
                                        // mapper detail / browse mapper
                                    },
                                    verified = if (map is BSMapVO) map.uploader.verifiedMapper?.let { true } == true else false,
                                    avatarUrl = (map as BSMapVO).uploader.avatar
                                )
                            }
                            DateLabel(date = (map as BSMapVO).map.createdAt)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                BSDurationLabel(duration = map.getDuration())
                                Spacer(modifier = Modifier.width(8.dp))
                                BSBPMLabel(bpm = map.getBPM())
                            }
                            // rating
                            Row {
                                BSThumbUpLabel(map.map.upVotes)
                                BSThumbDownLabel(map.map.downVotes)
                                BSRatingLabel(rating = map.map.score)
                            }
                        }
                    }
                    // curator
                    if ((map as BSMapVO).curator != null) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Curated By: ",
                                style = MaterialTheme.typography.labelMedium
                            )
                            MapperLabel(
                                mapperName = map.curator?.name!!,
                                verified = map.curator?.verifiedMapper?.let { true } == true,
                                avatarUrl = map.curator?.avatar
                            )
                        }
                    }
                    MapTags(map.map.tags)
                }
                Column(
                    Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = map.getMapDescription(),
                        modifier = Modifier
                            .padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,

                        )
                }
        }

        Column {
            Text(
                text = "Difficulties",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleLarge
            )
            BSMapDifficulties((map as BSMapVO).versions.firstOrNull()?.diffs, Modifier.padding(horizontal = 16.dp))

        }
        Column {
            Text(
                text = "Comments",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleLarge
            )
            // comments
            Column(

                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                if (comments.isEmpty()) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "No Comments",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }else {
                    comments.forEach { comment ->
                        Column() {
                            Row (
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                MapperLabel(
                                    comment.creator.name,
                                    comment.creator.avatar,
                                    comment.creator.verifiedMapper?.let { true } == true
                                )
                                // Positive / Negative
                                when(comment.sentiment) {
                                    BSMapReviewSentiment.POSITIVE -> {
                                        Icon(
                                            imageVector = Icons.Rounded.Favorite,
                                            contentDescription = "Thumb Up Icon",
                                            modifier = Modifier.size(24.dp).padding(start = 8.dp),
                                            tint = Color.Red
                                        )
                                    }
                                    BSMapReviewSentiment.NEGATIVE -> {
                                        Icon(
                                            imageVector = Icons.Rounded.HeartBroken,
                                            contentDescription = "Thumb Up Icon",
                                            modifier = Modifier.size(24.dp).padding(start = 8.dp),
                                            tint = Color.DarkGray
                                        )
                                    }
                                }

                                Text(
                                    text = comment.createdAt.prettyFormat(),
                                    modifier = Modifier.padding(start = 4.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Text(
                                text = comment.text,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }
    }
}