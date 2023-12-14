package io.ktlab.bshelper.ui.components.labels

//import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.ktlab.bshelper.model.vo.MapDiff


@Composable
fun MapDiffLabel(diff: MapDiff) {
    Row(
        modifier=Modifier.padding(4.dp),
    ) {
        Spacer(modifier = Modifier.padding(2.dp))
        if (diff.hasEasy()) {
            Text(text="E", color= Color(0xFF009f73),fontWeight = FontWeight.Bold, softWrap = false)
            Spacer(modifier = Modifier.padding(2.dp))
        }
        if (diff.hasNormal()) {
            Text(text="N", color= Color(0xFF1268A1),fontWeight = FontWeight.Bold, softWrap = false)
            Spacer(modifier = Modifier.padding(2.dp))
        }
        if (diff.hasHard()) {
            Text(text="H", color= Color(0xFFFFA500),fontWeight = FontWeight.Bold, softWrap = false)
            Spacer(modifier = Modifier.padding(2.dp))
        }
        if (diff.hasExpert()) {
            Text(text="EX", color= Color(0xFFBB86FC),fontWeight = FontWeight.Bold, softWrap = false)
            Spacer(modifier = Modifier.padding(2.dp))
        }
        if (diff.hasExpertPlus()) {
            Text(text="EX+", color= Color(0xFFB52A1C),fontWeight = FontWeight.Bold, softWrap = false)
            Spacer(modifier = Modifier.padding(2.dp))
        }
    }
}

//@Composable
//@Preview
//fun PreviewMapDiffLabel() {
//    Column {
//        MapDiffLabel(MapDiff(0b11111))
//    }
//}