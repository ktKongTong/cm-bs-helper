package io.ktlab.bshelper.ui.screens.beatsaver

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import io.ktlab.bshelper.ui.screens.beatsaver.components.BSMapperOverview
import io.ktlab.bshelper.viewmodel.BeatSaverUIEvent
import io.ktlab.bshelper.viewmodel.BeatSaverUiState

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun BSMapperScreen(
    uiState: BeatSaverUiState,
    snackbarHostState: SnackbarHostState,
    onUIEvent: (io.ktlab.bshelper.ui.event.UIEvent) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    lazyGridState: LazyGridState = rememberLazyGridState(),
) {

//    uiState as BeatSaverUiState.MapperQuery
    val mapperPagingItems = uiState.mapperFlow.collectAsLazyPagingItems()
    Row {
    if (mapperPagingItems.loadState.refresh is LoadState.Error) {
        LaunchedEffect(key1 = snackbarHostState) {
            snackbarHostState.showSnackbar(
                (mapperPagingItems.loadState.refresh as LoadState.Error).error.message ?: ""
            )
        }
    }
    val windowSizeClass = calculateWindowSizeClass().widthSizeClass
    val size = when (windowSizeClass) {
        WindowWidthSizeClass.Expanded -> 2
        else -> 1
    }
    Box(Modifier.fillMaxSize()) {
        Column {
            Row {
                Text("Mappers", style = MaterialTheme.typography.headlineLarge)
            }
            if (mapperPagingItems.loadState.refresh is LoadState.Loading) {
                Box(
                    modifier = Modifier
                        .widthIn(min = 64.dp, max = 128.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(size),
                    // remember scroll state to scroll to the position when items count changes
                    contentPadding = contentPadding,
                    state = lazyGridState,
                ) {
                    items(
                        count = mapperPagingItems.itemCount,
                        key = mapperPagingItems.itemKey { it.id },
                        span = { GridItemSpan(1) }
                    ) { index ->
                        val mapper = mapperPagingItems[index]
                        if (mapper != null) {
                            BSMapperOverview(
                                bsMapper = mapper,
                                onClick = { onUIEvent(BeatSaverUIEvent.OnSelectedBSMapper(mapper.id)) }
                            )
                        }


                    }
                    item {
                        if (mapperPagingItems.loadState.append is LoadState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        } else if (mapperPagingItems.loadState.append.endOfPaginationReached) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "😲 no more data", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

        }
    }
}
}