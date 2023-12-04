package io.ktlab.bshelper.ui.components

//import coil.compose.AsyncImage
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.painterResource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktlab.bshelper.MR
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
    val proxiedSource = source
//        .replace(Url(source).host, "beatsaver.wgzeyu.vip/cdn")

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
//            fallback()
            KamelImage(
                modifier = modifier,
                resource = asyncPainterResource(proxiedSource),
                contentDescription = "Profile",
                onLoading = { progress -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                    )
                } },
                onFailure = { throwable -> fallback() },
            )
        }
        "file" -> {

            fallback()
//            val painter = BitmapPainter(BitmapFactory.decodeFile(source).asImageBitmap())
//            KamelImage(
//                resource = asyncPainterResource(source),
//                contentDescription = contentDescription,
//                modifier = modifier
//            )
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

