package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.enums.MapTagType
import io.ktlab.bshelper.model.enums.MapTag as MapTagEnum


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MapTags(
    tags:List<String>,
    modifier: Modifier = Modifier,
) {
    FlowRow {
        io.ktlab.bshelper.model.enums.MapTag.sort(tags).map {
            MapTag(tag = it)
        }
    }
}

@Composable
fun MapTag(
    tag:String
) {
    MapTagEnum.fromSlug(tag)?.let {
        when(it.type) {
            MapTagType.Style -> StyleMapTag(it.human)
            MapTagType.Genre -> GenreMapTag(it.human)
            else -> {}
        }
    }
}

@Composable
private fun StyleMapTag(
    tag:String
) {
    Row(
        modifier = Modifier
            .padding(3.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Red, RoundedCornerShape(4.dp))
            .padding(3.dp)
    ) {
        Text(text = tag, softWrap = false, maxLines = 1, color = Color.White, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun GenreMapTag(
    tag:String
) {
    Row(
        modifier = Modifier
            .padding(3.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF144599), RoundedCornerShape(4.dp))
            .padding(3.dp)

    ) {
        Text(text = tag, softWrap = false, maxLines = 1, color = Color.White,style = MaterialTheme.typography.labelSmall)
    }
}