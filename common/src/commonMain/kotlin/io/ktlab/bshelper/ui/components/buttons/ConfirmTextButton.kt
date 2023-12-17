package io.ktlab.bshelper.ui.components.buttons

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import io.ktlab.bshelper.MR

@Composable
fun ConfirmTextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(Icons.Rounded.Check, stringResource(MR.strings.confirm))
        Text(
            modifier = Modifier.padding(start = 2.dp),
            text = stringResource(MR.strings.confirm)
        )
    }
}


@Composable
fun QueryTextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(Icons.Rounded.Search, stringResource(MR.strings.search))
        Text(
            modifier = Modifier.padding(start = 2.dp),
            text = stringResource(MR.strings.search)
        )
    }
}

@Composable
fun ClearTextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    TextButton(onClick = onClick, modifier = modifier) {
        Icon(Icons.Filled.Clear, stringResource(MR.strings.clear))
        Text(
            modifier = Modifier.padding(start = 2.dp),
            text = stringResource(MR.strings.clear)
        )
    }
}
