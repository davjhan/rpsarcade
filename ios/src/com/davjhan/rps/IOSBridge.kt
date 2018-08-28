package com.davjhan.rps

import com.badlogic.gdx.Gdx
import com.davjhan.rps.data.Analytics
import com.davjhan.rps.data.Bridge
import ktx.log.info
import org.robovm.apple.foundation.NSDictionary
import org.robovm.apple.foundation.NSObject
import org.robovm.apple.foundation.NSString
import org.robovm.apple.uikit.UIApplication
import org.robovm.pods.firebase.analytics.FIRAnalytics
import org.robovm.pods.google.mobileads.GADInterstitial
import org.robovm.pods.google.mobileads.GADInterstitialDelegateAdapter
import org.robovm.pods.google.mobileads.GADRequest
import org.robovm.pods.google.mobileads.GADRequestError


class IOSBridge : Bridge {
    override fun openRateLink() {
        val appId = "1420634125"
        val uri = "itms-apps://itunes.apple.com/app/$appId"
        Gdx.net.openURI(uri)
    }


    var interstitialId = "ca-app-pub-7872626554261230/9672718349"
    var interstitial: GADInterstitial? = null
    override fun loadAd() {
        if (interstitial != null) {
            if (!interstitial!!.hasBeenUsed() && interstitial!!.isReady) return
        }
        interstitial = GADInterstitial(interstitialId)
        val request = GADRequest()
//        request.testDevices = asList(GADRequest.getSimulatorID(),
//                "97e455e60254d600c884fc3e08ebe7a9")
        interstitial!!.loadRequest(request)
    }


    override fun reportAnalytic(category: String,
                                action: String,
                                label: String?,
                                value: Int?) {
        val dict = HashMap<NSString, NSString>()
        dict.put(NSString("category"), NSString(category))
        if (label != null)        dict.put(NSString("label"),NSString(label))
        if (value != null)        dict.put(NSString("value"),NSString(value.toString()))

        FIRAnalytics.logEventWithName(action, NSDictionary<NSString, NSString>(dict))
        info("tttt [IOSBridge]") { "Reported Analytic: $category, $action, $label, $value" }
    }

    override fun showAd(after: (didShow: Boolean) -> Unit) {
        info("tttt [IOSBridge]") { "Ad show requested." }
        if (interstitial != null && interstitial!!.isReady) {
            interstitial!!.delegate = object : GADInterstitialDelegateAdapter() {
                override fun shouldChangeAudioSessionToCategory(ad: NSObject?, audioSessionCategory: String?): Boolean {
                    return true
                }

                override fun didDismissScreen(ad: GADInterstitial?) {
                    after(true)
                    loadAd()
                }

                override fun didFailToPresentScreen(ad: GADInterstitial?) {
                    after(false)
                    loadAd()
                }

                override fun willLeaveApplication(ad: GADInterstitial?) {
                    info("tttt [IOSBridge]") { "left game" }
                }

                override fun didFailToReceiveAd(ad: GADInterstitial?, error: GADRequestError?) {
                    super.didFailToReceiveAd(ad, error)
                    info("tttt [IOSBridge]") { "ad load failed" }
                }
            }

            val viewController = UIApplication.getSharedApplication().getKeyWindow().getRootViewController()
            interstitial!!.present(viewController)

            reportAnalytic(Analytics.Category.ads, Analytics.Action.adShown)
            info("tttt [IOSBridge]") { "Showing Ad." }
            interstitial = null
        } else {
            info("tttt [IOSBridge]") { "Ad not loaded. Loading." }
            loadAd()
            after(false)
        }
    }
}