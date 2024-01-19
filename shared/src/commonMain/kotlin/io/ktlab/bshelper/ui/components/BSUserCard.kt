package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import io.ktlab.bshelper.model.BSUser
import io.ktlab.bshelper.model.dto.BSUserWithStatsDTO

@Composable
fun BSUserCard (
    bSUser: BSUserWithStatsDTO,
) {
    Card {
        Row {
            AsyncImageWithFallback(
                source = bSUser.avatar,
            )
            Column {
                Text(text = bSUser.name, style = MaterialTheme.typography.titleLarge)
                Text(text = bSUser.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis)
            }
        }
//        LazyVerticalGrid()

    }
}