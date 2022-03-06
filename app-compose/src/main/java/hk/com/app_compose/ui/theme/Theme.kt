package hk.com.app_compose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import hk.com.app_compose.R

@Composable
private fun darkColorPalette() = darkColors(
    primary = colorResource(id = R.color.purple_200),
    primaryVariant = colorResource(id = R.color.purple_700),
    secondary = colorResource(id = R.color.teal_200)
)

@Composable
private fun lightColorPalette() = lightColors(
    primary = colorResource(id = R.color.purple_500),
    primaryVariant = colorResource(id = R.color.purple_700),
    secondary = colorResource(id = R.color.teal_200)

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun L0LAMoveTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        darkColorPalette()
    } else {
        lightColorPalette()
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}