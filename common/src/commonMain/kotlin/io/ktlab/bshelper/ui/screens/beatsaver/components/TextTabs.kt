package io.ktlab.bshelper.ui.screens.beatsaver.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.viewmodel.TabType


@Composable
fun TextTabs(
    selectedTab : TabType,
    onClickTab: (TabType) -> Unit
) {
    Column {

        TabRow(
            selectedTabIndex = TabType.getIndexOf(selectedTab),
            containerColor = MaterialTheme.colorScheme.background,
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[TabType.getIndexOf(selectedTab)])
                        .height(4.dp)
                        .padding(horizontal = 28.dp)
                        .background(color = MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                )
            },
            divider = {}
        ) {
            TabType.tabs.forEachIndexed { index, tabType ->
                Tab(
                    selected = selectedTab == TabType.fromIndex(index),
                    onClick = { onClickTab(tabType) },
                    text = { Text(text = tabType.human, overflow = TextOverflow.Ellipsis) }
                )
            }
        }
    }
}
