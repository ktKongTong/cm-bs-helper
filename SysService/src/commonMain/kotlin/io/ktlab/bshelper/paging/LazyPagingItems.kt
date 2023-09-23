package io.ktlab.bshelper.paging

import kotlinx.coroutines.Dispatchers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.paging.CombinedLoadStates
import androidx.paging.DifferCallback
import androidx.paging.LOGGER
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.Logger
import androidx.paging.NullPaddedList
import androidx.paging.PagingData
import androidx.paging.PagingDataDiffer
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.withContext

class LazyPagingItems<T : Any> internal constructor(
    private val flow: Flow<PagingData<T>>
) {
    private val mainDispatcher = Dispatchers.Default

    private val differCallback: DifferCallback = object : DifferCallback {
        override fun onChanged(position: Int, count: Int) {
            if (count > 0) {
                updateItemSnapshotList()
            }
        }

        override fun onInserted(position: Int, count: Int) {
            if (count > 0) {
                updateItemSnapshotList()
            }
        }

        override fun onRemoved(position: Int, count: Int) {
            if (count > 0) {
                updateItemSnapshotList()
            }
        }
    }

    private val pagingDataDiffer = object : PagingDataDiffer<T>(
        differCallback = differCallback,
        mainContext = mainDispatcher,
        cachedPagingData =
        if (flow is SharedFlow<PagingData<T>>) flow.replayCache.firstOrNull() else null
    ) {
        override suspend fun presentNewList(
            previousList: NullPaddedList<T>,
            newList: NullPaddedList<T>,
            lastAccessedIndex: Int,
            onListPresentable: () -> Unit
        ): Int? {
            onListPresentable()
            updateItemSnapshotList()
            return null
        }
    }


    var itemSnapshotList by mutableStateOf(
        pagingDataDiffer.snapshot()
    )
        private set

    val itemCount: Int get() = itemSnapshotList.size

    private fun updateItemSnapshotList() {
        itemSnapshotList = pagingDataDiffer.snapshot()
    }


    operator fun get(index: Int): T? {
        pagingDataDiffer[index] // this registers the value load
        return itemSnapshotList[index]
    }

    fun peek(index: Int): T? {
        return itemSnapshotList[index]
    }

    fun retry() {
        pagingDataDiffer.retry()
    }

    fun refresh() {
        pagingDataDiffer.refresh()
    }


    internal suspend fun collectLoadState() {
        pagingDataDiffer.loadStateFlow.filterNotNull().collect {
            loadState = it
        }
    }

    internal suspend fun collectPagingData() {
        flow.collectLatest {
            pagingDataDiffer.collectFrom(it)
        }
    }

    private companion object {
        init {
            LOGGER = LOGGER ?: object : Logger {
                override fun isLoggable(level: Int): Boolean {
//                    return Log.isLoggable(LOG_TAG, level)
                    return false
                }

                override fun log(level: Int, message: String, tr: Throwable?) {
//                    when {
//                        tr != null && level == Log.DEBUG -> Log.d(LOG_TAG, message, tr)
//                        tr != null && level == Log.VERBOSE -> Log.v(LOG_TAG, message, tr)
//                        level == Log.DEBUG -> Log.d(LOG_TAG, message)
//                        level == Log.VERBOSE -> Log.v(LOG_TAG, message)
//                        else -> {
//                            throw IllegalArgumentException(
//                                "debug level $level is requested but Paging only supports " +
//                                        "default logging for level 2 (DEBUG) or level 3 (VERBOSE)"
//                            )
//                        }
//                    }
//                }
                }
            }
        }
    }

    private val IncompleteLoadState = LoadState.NotLoading(false)
    private val InitialLoadStates = LoadStates(
        LoadState.Loading,
        IncompleteLoadState,
        IncompleteLoadState
    )

    /**
     * A [CombinedLoadStates] object which represents the current loading state.
     */
    public var loadState: CombinedLoadStates by mutableStateOf(
        pagingDataDiffer.loadStateFlow.value
            ?: CombinedLoadStates(
                refresh = InitialLoadStates.refresh,
                prepend = InitialLoadStates.prepend,
                append = InitialLoadStates.append,
                source = InitialLoadStates
            )
    )
        private set
}

@Composable
fun <T : Any> Flow<PagingData<T>>.collectAsLazyPagingItems(
    context: CoroutineContext = EmptyCoroutineContext
): LazyPagingItems<T> {

    val lazyPagingItems = remember(this) { LazyPagingItems(this) }

    LaunchedEffect(lazyPagingItems) {
        if (context == EmptyCoroutineContext) {
            lazyPagingItems.collectPagingData()
        } else {
            withContext(context) {
                lazyPagingItems.collectPagingData()
            }
        }
    }

    LaunchedEffect(lazyPagingItems) {
        if (context == EmptyCoroutineContext) {
            lazyPagingItems.collectLoadState()
        } else {
            withContext(context) {
                lazyPagingItems.collectLoadState()
            }
        }
    }

    return lazyPagingItems
}