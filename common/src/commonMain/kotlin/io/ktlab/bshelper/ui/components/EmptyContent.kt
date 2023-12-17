package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import io.ktlab.bshelper.MR

@Composable
fun EmptyContent(modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxHeight().fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(MR.images.home_empty_list),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .height(150.dp)
                        .width(150.dp),
            )
            Text(
                stringResource(MR.strings.empty_content),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h4,
                modifier =
                    Modifier
                        .padding(top = 16.dp),
            )
        }
    }
}
