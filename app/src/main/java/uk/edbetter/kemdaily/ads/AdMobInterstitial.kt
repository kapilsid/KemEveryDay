package uk.edbetter.kemdaily.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.ConsentInformation
import com.google.android.ump.UserMessagingPlatform
import uk.edbetter.kemdaily.BuildConfig

/** Loads one interstitial ahead of time and shows it only between Daily boards, never during play. */
object AdMobInterstitial {
    private const val TAG = "AdMobInterstitial"
    private var interstitial: InterstitialAd? = null
    private var isLoading = false
    private var lastShownAt = 0L

    /** Leaving and re-opening a finished board should not show a second ad straight away. */
    private const val MIN_INTERVAL_MS = 3 * 60 * 1000L

    fun initialize(activity: Activity) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)
        val requestParameters = ConsentRequestParameters.Builder().build()
        consentInformation.requestConsentInfoUpdate(
            activity,
            requestParameters,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                    if (consentInformation.canRequestAds()) initializeAds(activity.applicationContext)
                }
            },
            {
                if (consentInformation.canRequestAds()) initializeAds(activity.applicationContext)
            },
        )
    }

    private fun initializeAds(context: Context) {
        MobileAds.initialize(context) { load(context.applicationContext) }
    }

    fun load(context: Context) {
        if (isLoading || interstitial != null) return
        isLoading = true
        InterstitialAd.load(
            context,
            BuildConfig.ADMOB_INTERSTITIAL_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitial = ad
                    isLoading = false
                    Log.d(TAG, "Interstitial loaded")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitial = null
                    isLoading = false
                    Log.d(TAG, "Interstitial failed to load: ${error.message}")
                }
            },
        )
    }

    /** True when the user is in a region (such as the UK or EEA) where Google requires a privacy options entry point. */
    fun privacyOptionsRequired(context: Context): Boolean =
        UserMessagingPlatform.getConsentInformation(context).privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    fun showPrivacyOptions(activity: Activity) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { error ->
            if (error != null) {
                Log.d(TAG, "Privacy options unavailable: ${error.message}")
                Toast.makeText(activity, "Ad privacy options are not required on this device", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** Continues immediately when no ad is ready, so the player is never blocked. */
    fun show(activity: Activity, onFinished: () -> Unit) {
        val ad = interstitial
        if (ad == null || System.currentTimeMillis() - lastShownAt < MIN_INTERVAL_MS) {
            load(activity.applicationContext)
            onFinished()
            return
        }

        interstitial = null
        lastShownAt = System.currentTimeMillis()
        var continued = false
        fun continueOnce() {
            if (continued) return
            continued = true
            load(activity.applicationContext)
            onFinished()
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() = continueOnce()
            override fun onAdFailedToShowFullScreenContent(error: AdError) = continueOnce()
        }
        ad.show(activity)
    }
}
