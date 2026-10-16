package uk.edbetter.kemdaily

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory

/**
 * Debug builds (emulator, Android Studio) use the App Check debug provider. On first run it logs a
 * debug token to Logcat ("DebugAppCheckProvider"); add that token in Firebase console → App Check →
 * Manage debug tokens so this device can still reach Firestore while enforcement is on.
 */
object AppCheckSetup {
    fun install(context: Context) {
        FirebaseApp.initializeApp(context)
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
    }
}
