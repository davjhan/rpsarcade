package com.davjhan.rps

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import com.badlogic.gdx.backends.android.AndroidApplication
import com.davjhan.rps.data.Analytics
import com.davjhan.rps.data.Bridge
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import ktx.log.info


class AndroidBridge(val androidApp: AndroidApplication) : Bridge {


    override fun openRateLink() {
        startActivity(androidApp.context, Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.davjhan.rps")), null)
    }

    var interstitialId = "ca-app-pub-7872626554261230/4471065380"
    var interstitial: InterstitialAd = InterstitialAd(androidApp.context)

    init {
        MobileAds.initialize(androidApp.context,
                "ca-app-pub-7872626554261230~8410310397")
        interstitial.adUnitId = interstitialId
        androidApp.runOnUiThread{
            interstitial.setImmersiveMode(true)
        }

    }

    override fun loadAd() {
        if (interstitial.isLoading) return
        androidApp.runOnUiThread {
            info("tttt [AndroidBridge]") { "Loading Ad" }
            interstitial.loadAd(AdRequest.Builder()
                    .addTestDevice("75AD639D19B49B022B1911E4AE5FC03E").build())
        }

    }

    override fun reportAnalytic(category: String, action: String, label: String?, value: Int?) {
        val analytics = FirebaseAnalytics.getInstance(androidApp.context)
        val bundle = Bundle()
        bundle.putString("category", category)
        if(label != null)bundle.putString("label", label)
        if(label != null)bundle.putString("value", value.toString())
        analytics.logEvent(action, bundle)
        info("tttt [IOSBridge]") { "Reported Analytic: $category, $action, $label, $value" }

    }


    override fun showAd(after: (didShow: Boolean) -> Unit) {
        info("tttt [IOSBridge]") { "Ad show requested." }
        androidApp.runOnUiThread {
            if (interstitial.isLoaded) {
                interstitial.adListener = object : AdListener() {
                    override fun onAdClosed() {
                        super.onAdClosed()
                        info("tttt [IOSBridge]") { "Showing Ad." }
                        after(true)

                        loadAd()
                    }

                    override fun onAdFailedToLoad(p0: Int) {
                        ktx.log.info ("tttt [AndroidBridge]"){""}
                        info ("tttt [AndroidBridge]"){"load Failed $p0"}
                        super.onAdFailedToLoad(p0)
                        after(false)
                        loadAd()
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        info("tttt [AndroidBridge]") { "AD LOADED ${interstitial.isLoading}" }
                    }
                }
                reportAnalytic(Analytics.Category.ads, Analytics.Action.adShown)
                interstitial.show()


            } else {
                info("tttt [IOSBridge]") { "Ad not loaded. Loading." }
                loadAd()
                after(false)
            }
        }
    }


}