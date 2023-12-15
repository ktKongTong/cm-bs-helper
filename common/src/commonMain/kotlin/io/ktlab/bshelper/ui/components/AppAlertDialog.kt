package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties

@Composable
fun AppAlertDialog(
    title: String,
    text: String = "",
    openDialog: Boolean = false,
    onClose: () -> Unit,
    onConfirm: () -> Unit = {},
    onCancel: (() -> Unit)? = null,
    triggerBy: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    triggerBy()
    if (openDialog) {
        AlertDialog(
            onDismissRequest = {onClose()},
            dismissButton = {
                if (onCancel != null) {
                    Text(text = "取消",Modifier.clickable {
                            onClose()
                            onCancel()
                    })
                }
            },
            confirmButton = {
                Text(text = "确定",Modifier.clickable {
                    onClose()
                    onConfirm()
                })
            },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = title)
                }
            },
            text = {
                if (text.isNotEmpty()) {
                    Text(text = text)
                }else {
                    content()
                }
            },
//            overlay mask
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            )
        )
    }

}

//
//@Preview
//@Composable
//fun AppAlertDialogPreview() {
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable {  }
//    ) {
//
//        AppAlertDialog(
//            title = "title",
//            text = "this is a dialog",
//            openDialog = true,
//            onConfirm = {},
//            onCancel = {},
//            onClose = {},
//            triggerBy = {
//            }
//        )
//        Dialog(onDismissRequest = { /*TODO*/ }) {
//            Card {
//                Text(text = "this is a dialog")
//            }
//        }
//
//    }
//
//}