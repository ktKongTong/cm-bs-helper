package io.ktlab.bshelper.ui.screens.home.bsmap

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MoveDown
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun MapCardMenu(
    modifier: Modifier,
    onDelete: ()->Unit,
    onMove: ()->Unit = {},
    onPreview: ()->Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }
//    val context = LocalContext.current
    Box(
        modifier = modifier
    ) {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(text = "删除") },
                onClick = { onDelete();expanded = false },
                leadingIcon = {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove Map"
                    )
            })
            DropdownMenuItem(
                text = { Text(text = "移动") },
                onClick = {
//                    Toast.makeText(context, "🥹 sorry! not yet implemented! please use multi move first", Toast.LENGTH_SHORT).show()
//                    onMove()
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.MoveDown,
                        contentDescription = "Move Map"
                    )
                })
            DropdownMenuItem(
                text = { Text(text = "预览") },
                onClick = {
                    onPreview()
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Preview,
                        contentDescription = "Preview Map"
                    )
                })
        }
    }
}