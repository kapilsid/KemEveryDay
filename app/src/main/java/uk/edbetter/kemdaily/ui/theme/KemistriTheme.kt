package uk.edbetter.kemdaily.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Navy = Color(0xFF082153)
val Sky = Color(0xFF12BFEA)
val Green = Color(0xFF09AE67)
val Lime = Color(0xFF77D23C)
val Cream = Color(0xFFFFFBF0)
val Coral = Color(0xFFF33D6B)
val Gold = Color(0xFFFFB51B)
val SoftBlue = Color(0xFFEAFBFF)

private val Colors = lightColorScheme(
    primary = Green,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8F7E6),
    secondary = Sky,
    onSecondary = Color.White,
    tertiary = Gold,
    background = SoftBlue,
    onBackground = Navy,
    surface = Color.White,
    onSurface = Navy,
    error = Coral,
)

@Composable
fun KemistriTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Colors, typography = Typography(), content = content)
}
