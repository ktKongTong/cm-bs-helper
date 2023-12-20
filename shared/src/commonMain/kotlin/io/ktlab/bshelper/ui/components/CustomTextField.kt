package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp


@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    value: String,
    placeholder: (@Composable () -> Unit)? = null,
    onValueChange : (String) -> Unit = {},
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
) {
    BasicTextField(
        modifier = modifier
            .fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        maxLines = 1,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = textStyle,
        decorationBox = { innerTextField ->
            Row(
                modifier
                    .border(1.dp,MaterialTheme.colorScheme.primary,MaterialTheme.shapes.large)
                    .padding(4.dp)
                    .padding(vertical = 8.dp)
                ,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) leadingIcon()
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) placeholder?.let { it() }
                    innerTextField()
                }
                if (trailingIcon != null) trailingIcon()
            }
        },
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions
    )
}