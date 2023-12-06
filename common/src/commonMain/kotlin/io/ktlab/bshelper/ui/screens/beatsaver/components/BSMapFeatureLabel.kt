package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.ktlab.bshelper.model.vo.BSMapVO
import io.ktlab.bshelper.ui.components.icons.*


fun BSMapVO.hasNoodleExtensions(): Boolean {
    return versions.firstOrNull()?.diffs?.any { it.ne?:false } ?: false
}

fun BSMapVO.hasMappingExtensions(): Boolean {
    return versions.firstOrNull()?.diffs?.any { it.me?:false } ?: false
}

fun BSMapVO.hasCinema(): Boolean {
    return versions.firstOrNull()?.diffs?.any { it.cinema?:false } ?: false
}

fun BSMapVO.hasChroma(): Boolean {
    return versions.firstOrNull()?.diffs?.any { it.chroma?:false } ?: false
}

@Composable
fun BSMapFeatureLabel(
    map: BSMapVO,
    modifier: Modifier = Modifier,
//    tint: Color = LocalContentColor.current
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        if (map.map.ranked) {
            BSRankedIcon()
        }
        if (map.map.automapper) {
            BSAIIcon()
        }
        if (map.curator != null) {
            BSCuratedIcon()
        }
        if (map.hasNoodleExtensions()) {
            BSNEIcon()
        }
        if (map.hasMappingExtensions()) {
            BSMEIcon()
        }
        if(map.hasCinema()) {
            BSCinemaIcon()
        }
        if (map.hasChroma()) {
            BSChromaIcon()
        }
    }
}