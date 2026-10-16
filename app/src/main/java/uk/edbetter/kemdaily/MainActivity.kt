package uk.edbetter.kemdaily

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import uk.edbetter.kemdaily.ads.AdMobInterstitial
import uk.edbetter.kemdaily.ui.KemistriApp
import uk.edbetter.kemdaily.ui.theme.KemistriTheme

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        val tag = newBase.getSharedPreferences("kemistri_everyday", Context.MODE_PRIVATE)
            .getString("language_tag", "system")
        super.attachBaseContext(LocaleManager.wrap(newBase, tag))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Must run before the ViewModel first touches Firebase Auth or Firestore.
        AppCheckSetup.install(applicationContext)
        enableEdgeToEdge()
        setContent { KemistriTheme { KemistriApp() } }
        AdMobInterstitial.initialize(this)
    }
}
