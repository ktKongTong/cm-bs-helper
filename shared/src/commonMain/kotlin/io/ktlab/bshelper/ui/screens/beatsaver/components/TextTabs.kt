package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.ui.viewmodel.TabType

@Composable
fun TextTabs(
    modifier: Modifier = Modifier,
    selectedTab: TabType,
    onClickTab: (TabType) -> Unit,
) {
    Column(modifier = modifier) {
        TabRow(
            selectedTabIndex = TabType.getIndexOf(selectedTab),
            indicator = { tabPositions ->
                Box(
                    modifier =
                        Modifier
                            .width(tabPositions[TabType.getIndexOf(selectedTab)].width)
                            .tabIndicatorOffset(tabPositions[TabType.getIndexOf(selectedTab)])
                            .height(4.dp)
                            .padding(horizontal = 32.dp)
                            .background(color = MaterialTheme.colorScheme.primary, MaterialTheme.shapes.large),
                )
            },
            divider = {},
        ) {
            TabType.tabs.forEachIndexed { index, tabType ->
                Tab(
                    selected = selectedTab == TabType.fromIndex(index),
                    onClick = { onClickTab(tabType) },
                    text = { Text(text = tabType.human, style = MaterialTheme.typography.labelLarge, overflow = TextOverflow.Ellipsis) },
                )
            }
        }
    }
}
