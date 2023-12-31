package io.ktlab.bshelper.ui.components

// import coil.compose.AsyncImage
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import dev.icerock.moko.resources.compose.painterResource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktlab.bshelper.MR
import io.ktlab.bshelper.ui.LocalUserPreference
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
    contentScale: ContentScale = ContentScale.Crop,
    type: String = getTypeBySource(source),
    alpha: Float = 1f,
    fallback: @Composable () -> Unit = {
        DefaultImage(modifier, contentScale, alpha)
    },
) {

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
            //
            val current = LocalUserPreference.current
            val proxiedSource = current.getProxiedImageSource(source)
            KamelImage(
                modifier = modifier,
                resource = asyncPainterResource(proxiedSource),
                contentDescription = "Profile",
                contentScale = contentScale,
                alpha = alpha,
                onLoading = { progress ->
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize(),
                    ) {
                        CircularProgressIndicator(
                            progress = progress,
                            modifier = Modifier,
                        )
                    }
                },
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

// @Preview
@Composable
fun DefaultImage(
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    alpha: Float = 1f,
) {
    Image(
        painter = painterResource(MR.images.home_empty_list),
        contentDescription = "default image",
        modifier = modifier,
        contentScale = contentScale,
        alpha = alpha,
    )
}
