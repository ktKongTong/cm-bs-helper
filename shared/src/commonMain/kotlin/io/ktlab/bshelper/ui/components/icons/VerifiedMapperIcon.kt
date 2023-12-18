package io.ktlab.bshelper.ui.components.icons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VerifiedMapperIcon(
    modifier: Modifier = Modifier,
    verified: Boolean,
    tint: Color = LocalContentColor.current,
) {
    Box {
        Icon(
            imageVector = Icons.Rounded.Person,
            contentDescription = "mapper icon",
            modifier = modifier.size(20.dp),
            tint = tint,
        )
        if (verified) {
            Icon(
                Icons.Filled.Verified,
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .size(12.dp)
                        .offset(x = 4.dp, y = (4).dp),
                contentDescription = "Verified Mapper Icon",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
