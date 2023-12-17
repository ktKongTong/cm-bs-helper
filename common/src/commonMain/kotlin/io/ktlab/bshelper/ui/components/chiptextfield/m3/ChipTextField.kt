package io.ktlab.bshelper.ui.components.chiptextfield.m3

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.ui.components.chiptextfield.core.BasicChipTextField
import io.ktlab.bshelper.ui.components.chiptextfield.core.Chip
import io.ktlab.bshelper.ui.components.chiptextfield.core.ChipStyle
import io.ktlab.bshelper.ui.components.chiptextfield.core.ChipTextFieldState

/**
 * Chip text field with Material Design filled style.
 *
 * The [innerModifier] will be passed to the inner text field of the decoration box. This can be
 * used to control style, layout and interaction of the inner text field independently.
 *
 * This is a sample to constraint the height of the inner text field and makes it scrollable:
 *
 * ```kotlin
 * ChipTextField(
 *     state = ...,
 *     onSubmit = ...,
 *     modifier = Modifier,
 *     innerModifier = Modifier
 *         .heightIn(max = 100.dp)
 *         .verticalScroll(state = rememberScrollState()),
 * )
 * ```
 *
 * @see [BasicChipTextField]
 * @see [TextField]
 */
@Composable
fun <T : Chip> ChipTextField(
    state: ChipTextFieldState<T>,
    onSubmit: (value: String) -> T?,
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    readOnlyChips: Boolean = readOnly,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    textStyle: TextStyle = LocalTextStyle.current,
    chipStyle: ChipStyle = ChipTextFieldDefaults.chipStyle(),
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    chipVerticalSpacing: Dp = 4.dp,
    chipHorizontalSpacing: Dp = 4.dp,
    chipLeadingIcon: @Composable (chip: T) -> Unit = {},
    chipTrailingIcon: @Composable (chip: T) -> Unit = { CloseButton(state, it) },
    onChipClick: ((chip: T) -> Unit)? = null,
    onChipLongClick: ((chip: T) -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = TextFieldDefaults.shape,
    colors: TextFieldColors = TextFieldDefaults.colors(),
    contentPadding: PaddingValues =
        if (label == null) {
            TextFieldDefaults.contentPaddingWithoutLabel()
        } else {
            TextFieldDefaults.contentPaddingWithLabel()
        }
) {
    var value by remember { mutableStateOf(TextFieldValue()) }
    val onValueChange: (TextFieldValue) -> Unit = { value = it }
    ChipTextField(
        state = state,
        onSubmit = { onSubmit(it.text) },
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        innerModifier = innerModifier,
        enabled = enabled,
        readOnly = readOnly,
        readOnlyChips = readOnlyChips,
        isError = isError,
        keyboardOptions = keyboardOptions,
        textStyle = textStyle,
        chipStyle = chipStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        chipVerticalSpacing = chipVerticalSpacing,
        chipHorizontalSpacing = chipHorizontalSpacing,
        chipLeadingIcon = chipLeadingIcon,
        chipTrailingIcon = chipTrailingIcon,
        onChipClick = onChipClick,
        onChipLongClick = onChipLongClick,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors,
        contentPadding = contentPadding,
    )
}

/**
 * Chip text field with Material Design filled style.
 *
 * The [innerModifier] will be passed to the inner text field of the decoration box. This can be
 * used to control style, layout and interaction of the inner text field independently.
 *
 * This is a sample to constraint the height of the inner text field and makes it scrollable:
 *
 * ```kotlin
 * ChipTextField(
 *     state = ...,
 *     onSubmit = ...,
 *     modifier = Modifier,
 *     innerModifier = Modifier
 *         .heightIn(max = 100.dp)
 *         .verticalScroll(state = rememberScrollState()),
 * )
 * ```
 *
 * @see [BasicChipTextField]
 * @see [TextField]
 */
@Composable
fun <T : Chip> ChipTextField(
    state: ChipTextFieldState<T>,
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: (value: String) -> T?,
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    readOnlyChips: Boolean = readOnly,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    textStyle: TextStyle = LocalTextStyle.current,
    chipStyle: ChipStyle = ChipTextFieldDefaults.chipStyle(),
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    chipVerticalSpacing: Dp = 4.dp,
    chipHorizontalSpacing: Dp = 4.dp,
    chipLeadingIcon: @Composable (chip: T) -> Unit = {},
    chipTrailingIcon: @Composable (chip: T) -> Unit = { CloseButton(state, it) },
    onChipClick: ((chip: T) -> Unit)? = null,
    onChipLongClick: ((chip: T) -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = TextFieldDefaults.shape,
    colors: TextFieldColors = TextFieldDefaults.colors(),
    contentPadding: PaddingValues =
        if (label == null) {
            TextFieldDefaults.contentPaddingWithoutLabel()
        } else {
            TextFieldDefaults.contentPaddingWithLabel()
        }
) {
    // Copied from androidx.compose.foundation.text.BasicTextField.kt
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    val textFieldValue = textFieldValueState.copy(text = value)
    SideEffect {
        if (textFieldValue.selection != textFieldValueState.selection ||
            textFieldValue.composition != textFieldValueState.composition
        ) {
            textFieldValueState = textFieldValue
        }
    }
    var lastTextValue by remember(value) { mutableStateOf(value) }
    val mappedOnValueChange: (TextFieldValue) -> Unit = { newTextFieldValueState ->
        textFieldValueState = newTextFieldValueState

        val stringChangedSinceLastInvocation = lastTextValue != newTextFieldValueState.text
        lastTextValue = newTextFieldValueState.text

        if (stringChangedSinceLastInvocation) {
            onValueChange(newTextFieldValueState.text)
        }
    }
    ChipTextField(
        state = state,
        onSubmit = { onSubmit(it.text) },
        value = textFieldValue,
        onValueChange = mappedOnValueChange,
        modifier = modifier,
        innerModifier = innerModifier,
        enabled = enabled,
        readOnly = readOnly,
        readOnlyChips = readOnlyChips,
        isError = isError,
        keyboardOptions = keyboardOptions,
        textStyle = textStyle,
        chipStyle = chipStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        chipVerticalSpacing = chipVerticalSpacing,
        chipHorizontalSpacing = chipHorizontalSpacing,
        chipLeadingIcon = chipLeadingIcon,
        chipTrailingIcon = chipTrailingIcon,
        onChipClick = onChipClick,
        onChipLongClick = onChipLongClick,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors,
        contentPadding = contentPadding,
    )
}

/**
 * Chip text field with Material Design filled style.
 *
 * The [innerModifier] will be passed to the inner text field of the decoration box. This can be
 * used to control style, layout and interaction of the inner text field independently.
 *
 * This is a sample to constraint the height of the inner text field and makes it scrollable:
 *
 * ```kotlin
 * ChipTextField(
 *     state = ...,
 *     onSubmit = ...,
 *     modifier = Modifier,
 *     innerModifier = Modifier
 *         .heightIn(max = 100.dp)
 *         .verticalScroll(state = rememberScrollState()),
 * )
 * ```
 *
 * @see [BasicChipTextField]
 * @see [TextField]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Chip> ChipTextField(
    state: ChipTextFieldState<T>,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onSubmit: (value: TextFieldValue) -> T?,
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    readOnlyChips: Boolean = readOnly,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    textStyle: TextStyle = LocalTextStyle.current,
    chipStyle: ChipStyle = ChipTextFieldDefaults.chipStyle(),
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    chipVerticalSpacing: Dp = 4.dp,
    chipHorizontalSpacing: Dp = 4.dp,
    chipLeadingIcon: @Composable (chip: T) -> Unit = {},
    chipTrailingIcon: @Composable (chip: T) -> Unit = { CloseButton(state, it) },
    onChipClick: ((chip: T) -> Unit)? = null,
    onChipLongClick: ((chip: T) -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = TextFieldDefaults.shape,
    colors: TextFieldColors = TextFieldDefaults.colors(),
    contentPadding: PaddingValues =
        if (label == null) {
            TextFieldDefaults.contentPaddingWithoutLabel()
        } else {
            TextFieldDefaults.contentPaddingWithLabel()
        }
) {
    val fieldColors = remember(colors) { colors.toChipTextFieldColors() }
    Box(
        modifier = modifier
            .background(
                fieldColors.backgroundColor(enabled, isError, interactionSource).value,
                shape
            )
            .indicatorLine(enabled, isError, interactionSource, colors)
    ) {
        BasicChipTextField(
            state = state,
            onSubmit = onSubmit,
            value = value,
            onValueChange = onValueChange,
            modifier = innerModifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            readOnlyChips = readOnlyChips,
            isError = isError,
            keyboardOptions = keyboardOptions,
            textStyle = textStyle,
            chipStyle = chipStyle,
            chipVerticalSpacing = chipVerticalSpacing,
            chipHorizontalSpacing = chipHorizontalSpacing,
            chipLeadingIcon = chipLeadingIcon,
            chipTrailingIcon = chipTrailingIcon,
            onChipClick = onChipClick,
            onChipLongClick = onChipLongClick,
            interactionSource = interactionSource,
            colors = fieldColors,
            decorationBox = { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = if (state.chips.isEmpty() && value.text.isEmpty()) "" else " ",
                    innerTextField = innerTextField,
                    enabled = !readOnly,
                    singleLine = false,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    isError = isError,
                    label = label,
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    colors = colors,
                    contentPadding = contentPadding,
                )
            },
        )
    }
}
