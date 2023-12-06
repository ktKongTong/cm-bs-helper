package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.MapDifficulty
import io.ktlab.bshelper.ui.components.BSMapDiffLabel
import io.ktlab.bshelper.ui.components.labels.*
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun BSMapDifficultyCard(
    diff: MapDifficulty
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.background(Color.Blue)
    ) {
        BSMapDiffLabel(diff.difficulty, characteristic = diff.characteristic)
        Row (verticalAlignment = Alignment.CenterVertically){
            BSLightEventLabel(diff.events!!)
            BSNPSLabel(diff.nps!!)
            BSNoteLabel(diff.notes!!)
            BSObstacleLabel(diff.obstacles!!)
            BSBombLabel(diff.bombs!!)
        }
    }
}

@Composable
fun BSMapDifficulties(
    diffs: List<MapDifficulty>? = null,
    modifier: Modifier = Modifier
){

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            diffs?.map {diff ->
                BSMapDiffLabel(diff.difficulty, characteristic = diff.characteristic)
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            diffs?.map {diff ->
                Row (verticalAlignment = Alignment.CenterVertically){
                    BSLightEventLabel(diff.events!!)
                    BSNPSLabel(diff.nps!!)
                    BSNoteLabel(diff.notes!!)
                    BSObstacleLabel(diff.obstacles!!)
                    BSBombLabel(diff.bombs!!)
                }
            }
        }

    }
}