package io.ktlab.bshelper.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import io.ktlab.bshelper.MR

@Composable
fun NextStepIconButton(onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Icon(Icons.Filled.Check, contentDescription = null)
        Text(text = stringResource(MR.strings.nextStep))
    }
}

@Composable
fun CancelButton(onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Icon(Icons.Filled.Cancel, contentDescription = null)
        Text(text = stringResource(MR.strings.cancel))
    }
}

@Composable
fun ConfirmButton(onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Icon(Icons.Filled.ConfirmationNumber, contentDescription = null)
        Text(text = stringResource(MR.strings.confirm))
    }
}
