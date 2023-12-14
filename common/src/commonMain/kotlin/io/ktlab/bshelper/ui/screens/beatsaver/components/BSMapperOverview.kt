package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.dto.BSUserWithStatsDTO
import io.ktlab.bshelper.ui.components.AsyncImageWithFallback
import io.ktlab.bshelper.ui.components.labels.MapperLabel

@Composable
fun BSMapperOverview(
    bsMapper:BSUserWithStatsDTO,
    onClick: () -> Unit,
) {
    Box(Modifier.padding(16.dp).clip(MaterialTheme.shapes.medium)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImageWithFallback(
                source = bsMapper.avatar,
                modifier = Modifier.size(64.dp).clip(CircleShape),
                contentDescription = "avatar"
            )
            Column() {
                MapperLabel(mapperName = bsMapper.name)
                Text(
                    bsMapper.description.ifEmpty { "mapper 在这里什么都没说..." },
                    modifier = Modifier.padding(start = 8.dp) ,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2)
            }
        }
    }
}