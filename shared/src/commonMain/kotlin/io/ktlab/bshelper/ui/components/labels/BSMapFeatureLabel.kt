package io.ktlab.bshelper.ui.components.labels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.vo.BSMapVO
import io.ktlab.bshelper.model.vo.FSMapVO
import io.ktlab.bshelper.ui.components.icons.*

fun IMap.hasNoodleExtensions(): Boolean {
    return when (this) {
        is BSMapVO -> versions.firstOrNull()?.diffs?.any { it.ne ?: false } ?: false
        is FSMapVO -> difficulties?.any { it.ne ?: false } ?: false
        else -> false
    }
}

fun IMap.hasMappingExtensions(): Boolean {
    return when (this) {
        is BSMapVO -> versions.firstOrNull()?.diffs?.any { it.me ?: false } ?: false
        is FSMapVO -> difficulties?.any { it.me ?: false } ?: false
        else -> false
    }
}

fun IMap.hasCinema(): Boolean {
    return when (this) {
        is BSMapVO -> versions.firstOrNull()?.diffs?.any { it.me ?: false } ?: false
        is FSMapVO -> difficulties?.any { it.cinema ?: false } ?: false
        else -> false
    }
}

fun IMap.hasChroma(): Boolean {
    return when (this) {
        is BSMapVO -> versions.firstOrNull()?.diffs?.any { it.me ?: false } ?: false
        is FSMapVO -> difficulties?.any { it.chroma ?: false } ?: false
        else -> false
    }
}

fun IMap.isRanked(): Boolean {
    return when (this) {
        is BSMapVO -> map.ranked
        is FSMapVO -> bsMapWithUploader?.bsMap?.ranked ?: false
        else -> false
    }
}

fun IMap.isAutoMapper(): Boolean {
    return when (this) {
        is BSMapVO -> map.automapper
        is FSMapVO -> bsMapWithUploader?.bsMap?.automapper ?: false
        else -> false
    }
}

@Composable
fun BSMapFeatureLabel(
    map: IMap,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        if (map.isRanked()) {
            BSRankedIcon()
        }
        if (map.isAutoMapper()) {
            BSAIIcon()
        }
        if (map.hasNoodleExtensions()) {
            BSNEIcon()
        }
        if (map.hasMappingExtensions()) {
            BSMEIcon()
        }
        if (map.hasCinema()) {
            BSCinemaIcon()
        }
        if (map.hasChroma()) {
            BSChromaIcon()
        }
    }
}
