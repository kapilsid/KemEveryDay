package uk.edbetter.kemdaily

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import uk.edbetter.kemdaily.ui.KemistriApp
import uk.edbetter.kemdaily.ui.theme.KemistriTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { KemistriTheme { KemistriApp() } }
    }
}
