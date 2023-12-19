package io.ktlab.bshelper.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.AnimatedDynamicMaterialTheme
import com.materialkolor.PaletteStyle
import io.ktlab.bshelper.BuildConfig

private val DarkColorPalette =
    darkColorScheme(
//    surfaceTint = ,
//    primary = Purple80,
//    secondary = PurpleGrey80,
//    tertiary = Pink80
    )

private val LightColorPalette =
    lightColorScheme(
//    surface = Blue,
//    secondaryContainer = LightBlue,
        primary = Blue,
//    surfaceTint = Color.Red,
//    onSurfaceVariant = Color.Red,
//    surfaceVariant = Color.Red,
//    surface = LightBlue,
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40
    )
val defaultThemeSeedColor = Color(BuildConfig.THEME_COLOR)

@Composable
fun BSHelperTheme(
    seedColor: Color = defaultThemeSeedColor,
    paletteStyle: PaletteStyle = PaletteStyle.TonalSpot,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        typography = Typography,
        shapes = Shapes,
    ) {
        AnimatedDynamicMaterialTheme(
            seedColor = seedColor,
            useDarkTheme = darkTheme,
            style = paletteStyle,
            content = {
                Surface(content = content)
            },
        )
    }
}
