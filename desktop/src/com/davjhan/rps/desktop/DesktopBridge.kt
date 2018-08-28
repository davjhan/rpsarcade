package com.davjhan.rps.desktop

import com.davjhan.rps.data.Bridge


class DesktopBridge: Bridge{
    override fun openRateLink() {

    }

    override fun reportAnalytic(category: String, action: String, label: String?, value: Int?) {
    }

    override fun showAd(after: (didShow: Boolean) -> Unit) {
        after(false)
    }

    override fun loadAd() {
    }


}