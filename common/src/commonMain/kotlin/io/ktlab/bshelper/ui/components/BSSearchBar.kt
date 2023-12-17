package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import dev.icerock.moko.resources.compose.stringResource
import io.ktlab.bshelper.MR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BSSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    Box(modifier.wrapContentHeight()) {
        SearchBar(
            modifier =
                Modifier
                    .align(Alignment.TopCenter),
            query = query,
            onQueryChange = onQueryChange,
            onSearch = { focusManager.clearFocus() },
            active = false,
            onActiveChange = {},
            placeholder = { Text(text = "Search") },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = "Search Icon") },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onClear() }) {
                        Icon(Icons.Rounded.Clear, stringResource(MR.strings.clear))
                    }
                }
            },
        ) {}
    }
}
