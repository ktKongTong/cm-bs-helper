package io.ktlab.bshelper.ui.components

import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberPopupPositionProviderAtPosition
import io.ktlab.bshelper.model.BSUser
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.MapDifficulty
import io.ktlab.bshelper.model.vo.BSMapVO
import io.ktlab.bshelper.model.vo.FSMapVO
import io.ktlab.bshelper.ui.components.icons.BSCharIcon
import io.ktlab.bshelper.ui.components.labels.BSBPMLabel
import io.ktlab.bshelper.ui.components.labels.BSBombLabel
import io.ktlab.bshelper.ui.components.labels.BSDurationLabel
import io.ktlab.bshelper.ui.components.labels.BSIDLabel
import io.ktlab.bshelper.ui.components.labels.BSLightEventLabel
import io.ktlab.bshelper.ui.components.labels.BSMapFeatureLabel
import io.ktlab.bshelper.ui.components.labels.BSNJSLabel
import io.ktlab.bshelper.ui.components.labels.BSNPSLabel
import io.ktlab.bshelper.ui.components.labels.BSNoteLabel
import io.ktlab.bshelper.ui.components.labels.BSObstacleLabel
import io.ktlab.bshelper.ui.components.labels.BSRatingLabel
import io.ktlab.bshelper.ui.components.labels.BSThumbDownLabel
import io.ktlab.bshelper.ui.components.labels.BSThumbUpLabel
import io.ktlab.bshelper.ui.components.labels.DateLabel
import io.ktlab.bshelper.ui.components.labels.MapperLabel
import io.ktlab.bshelper.ui.components.labels.SongNameLabel
@Composable
fun BSUserLabel(
    bsUser: BSUser,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    MapperLabel(
        mapperName = bsUser.name,
        onClick = {},
        verified = bsUser.verifiedMapper ?: false,
        avatarUrl = bsUser.avatar,
        modifier = modifier,
    )
}
@Composable
fun DiffCard(diff: MapDifficulty) {
    Column (modifier = Modifier.padding(2.dp)){
        Row (horizontalArrangement = Arrangement.spacedBy(4.dp)){
            Text(text = diff.characteristic.human)
            Text(text = diff.difficulty.human)
        }
        LazyVerticalGrid(
            modifier = Modifier.wrapContentSize(),
            columns = GridCells.Fixed(3),
        ) {
            item { BSNPSLabel(diff.nps?:0.0) }
            item { BSNJSLabel(diff.njs?:0.0) }
            item { BSLightEventLabel(diff.events?:0) }
            item { BSBombLabel(diff.bombs?:0) }
            item { BSNoteLabel(diff.notes?:0) }
            item { BSObstacleLabel(diff.obstacles?:0) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun MapDiffTag(
    diff: MapDifficulty
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val color = if (isHovered) Color.Red.copy(alpha = 1f) else Color.White
    val tooltipState = rememberTooltipState(isPersistent = true)
    LaunchedEffect(isHovered) {
        if (isHovered) {
            tooltipState.show()
        }
        else {
            tooltipState.dismiss()
        }
    }
    val modifier = Modifier
        .hoverable(interactionSource = interactionSource)
        .padding(4.dp)
        .border(1.dp, color, MaterialTheme.shapes.extraLarge)
        .padding(4.dp)
    val positionProvider = rememberPopupPositionProviderAtPosition(Offset(50F,-180F))
    TooltipBox(
        positionProvider = positionProvider,
        tooltip = {PlainTooltip { DiffCard(diff) }},
        state = tooltipState
    ) {
        Row (
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            BSCharIcon(
                characteristic = diff.characteristic,
                modifier = Modifier.padding(2.dp),
                tint = color,
            )
            Text(
                text = diff.difficulty.short,
                modifier = Modifier.padding(2.dp),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun MapItemV3(
    map: IMap,
    modifier: Modifier = Modifier,
    onLongClick: (IMap) -> Unit = {},
    onClick: (IMap) -> Unit = {},
    onAuthorClick: ((IMap) -> Unit)? = null,
    hoveredActionBar: @Composable () -> Unit = {},
    actionBar: @Composable () -> Unit = {},
) {
    val containerColor = MaterialTheme.colorScheme.surface
    Box(
        modifier =
        modifier
            .height(200.dp)
            .clip(shape = MaterialTheme.shapes.medium)
            .background(containerColor)
            .combinedClickable(onLongClick = { onLongClick(map) }, onClick = { onClick(map) })
            .fillMaxWidth(),
    ) {
        Row {
            val interactionSource = remember { MutableInteractionSource() }
            val isHovered = interactionSource.collectIsHoveredAsState()
            Box(
                Modifier
                    .widthIn(max = 200.dp)
                    .hoverable(interactionSource = interactionSource)
                    .height(200.dp),
            ) {
                AsyncImageWithFallback(
                    source = map.getAvatar(),
                    modifier = Modifier,
                    contentScale = ContentScale.FillWidth,
                    alpha = 0.8f,
                )
                if (isHovered.value) {
                    Canvas(
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                    ) {
                        drawRect(
                            alpha = 0.6f,
                            color = Color.Black,
                            blendMode = BlendMode.Darken
                        )
                    }
                    ProvideTextStyle(value = MaterialTheme.typography.labelSmall.copy(color = Color.White)){
                        Column {
                            // curator
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                when(map) {
                                    is FSMapVO -> map.bsMapWithUploader?.curator?.let { BSUserLabel(bsUser = it) }
                                    is BSMapVO -> map.curator?.let { BSUserLabel(bsUser = it) }
                                }
                            }
                            // id & feature
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier) {
                                BSIDLabel(id = map.getID(), tint = Color.White)
                                BSMapFeatureLabel(map = map, tint = Color.White)
                            }
                            // description
                            Text(
                                text = map.getMapDescription().ifEmpty { "No Description" },
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                                color = Color.White,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier.weight(1f))
                            // hard
                            FlowRow {
                                when (map) {
                                    is FSMapVO -> map.bsMapWithUploader?.difficulties?.forEach { MapDiffTag(it) }
                                    is BSMapVO -> map.versions.first().diffs.forEach { MapDiffTag(it) }
                                }
                            }
                            hoveredActionBar()
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row {
                    SongNameLabel(songName = map.getSongName())
                }
                MapperLabel(
                    mapperName = map.getAuthor(),
                    onClick = { onAuthorClick?.let { it(map) } },
                    verified =
                    when (map) {
                        is FSMapVO -> map.isVerified()
                        is BSMapVO -> map.uploader.verifiedMapper ?: false
                        else -> false
                    },
                    avatarUrl = map.getAuthorAvatar(),
                )
                map.getMapDate()?.let {
                    DateLabel(date = it)
                }
                if (map.isRelateWithBSMap()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
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
                    BSBPMLabel(bpm = map.getBPM())
                }

                when (map) {
                    is FSMapVO -> {
                        MapTags(tags = map.bsMapWithUploader?.bsMap?.tags ?: listOf())
                    }

                    is BSMapVO -> {
                        MapTags(tags = map.map.tags)
                    }
                }
                Spacer(modifier.weight(1f))
                actionBar()
            }
        }
    }
}