package io.ktlab.bshelper.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.painterResource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
//import coil.compose.AsyncImage
import io.ktlab.bshelper.MR
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private fun getTypeBySource(source: String): String {
    return when {
        source.startsWith("data:image") -> "base64"
        source.matches(Regex("^(http|https)://.*")) -> "network"
        else -> "image"
    }
}
@OptIn(ExperimentalEncodingApi::class)
@Composable
fun AsyncImageWithFallback(
    modifier: Modifier = Modifier,
    source: String,
    contentDescription: String? = null,
    type: String = getTypeBySource(source),
    fallback: @Composable () -> Unit = {
        DefaultImage(modifier)
    }
){
    when (type) {
//        "base64" -> {
//            val bytes= Base64.decode(source)
//            val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
//            val painter = BitmapPainter(Bitmap.createBitmap(bitmap).asImageBitmap())
//            Image(
//                painter = painter,
//                contentDescription = contentDescription,
//                modifier = modifier
//            )
//        }
        "network" -> {
            KamelImage(
                modifier = modifier,
                resource = asyncPainterResource(source),
                contentDescription = "Profile",
                onLoading = { progress -> Box {
                    CircularProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(RoundedCornerShape(50))
                    )
                    fallback()
                } },
                onFailure = { throwable -> /* Handle failure */ },
            )
        }
        "file" -> {
//            val painter = BitmapPainter(BitmapFactory.decodeFile(source).asImageBitmap())
            KamelImage(
                resource = asyncPainterResource(source),
                contentDescription = contentDescription,
                modifier = modifier
            )
        }
        else -> {
            fallback()
        }
    }
}

//@Preview
@Composable
fun DefaultImage(
    modifier: Modifier = Modifier
){
    Image(
        painter = painterResource(MR.images.home_empty_list),
        contentDescription = "default image",
        modifier = modifier
    )
}

