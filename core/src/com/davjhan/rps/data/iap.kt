package com.davjhan.rps.data

import com.badlogic.gdx.pay.*
import ktx.log.info

interface IAP{
    fun getIAPprice():String
    fun purchaseIAP(after:(opened:Boolean, purchased:Boolean,err:String?)->Unit)
    fun restorePurchase(after:(opened:Boolean, restored:Boolean, removedAds:Boolean,err:String?)->Unit)
}
class IAPImpl(val purchaseManager:PurchaseManager): IAP {
    val iapID = "removeads2"
    var onPurchase: ((opened:Boolean,purchased:Boolean, err:String?)->Unit)? = null
    var onRestore: ((opened:Boolean,restored:Boolean,removedAds:Boolean, err:String?)->Unit)? = null
    init{
        installPM()
    }
    private fun installPM(){
        val pmc = PurchaseManagerConfig()
        pmc.addOffer(Offer().setType(OfferType.ENTITLEMENT).setIdentifier(iapID))
        purchaseManager.install(object: PurchaseObserver {
            override fun handleRestore(transactions: Array<out Transaction>?) {
                val removedAds = transactions != null && transactions.isNotEmpty()
                onRestore?.let { it(true,true,removedAds,null) }
                onRestore = null
            }

            override fun handleRestoreError(e: Throwable?) {
                onRestore?.let { it(true,false,false,e?.cause?.message) }
                onRestore = null
            }

            override fun handlePurchaseCanceled() {
                onPurchase?.let { it(true,false,null) }
                onPurchase = null
            }

            override fun handlePurchaseError(e: Throwable?) {
                info ("tttt [IAPImpl]"){"purchase error ${e!!.localizedMessage}"}
                onPurchase?.let { it(true,false,e?.localizedMessage) }
                onPurchase = null
            }

            override fun handleInstall() {
                info ("tttt [IAPImpl]"){"Installed IAPImpl"}
            }

            override fun handlePurchase(transaction: Transaction?) {
                onPurchase?.let { it(true,transaction != null,null) }
                onPurchase = null
            }

            override fun handleInstallError(e: Throwable?) {
                info ("tttt [IAPImpl]"){"INSTALL ERROR ${e}"}
            }

        },pmc,true)
    }
    override fun getIAPprice():String{
        if (purchaseManager.installed()){
            val info = purchaseManager.getInformation(iapID)
            return info.localPricing
        }else{
            installPM()
            return ""
        }
    }
    override fun purchaseIAP(after:(opened:Boolean, purchased:Boolean,err:String?)->Unit){
        if (purchaseManager.installed()){
            onPurchase = after
            purchaseManager.purchase(iapID)
        }else{
            installPM()
            after(false,false,null)
        }
    }

    override fun restorePurchase(after:(opened:Boolean, restored:Boolean,removedAds:Boolean,err:String?)->Unit){
        if (purchaseManager.installed()){
            onRestore = after
            purchaseManager.purchaseRestore()
        }else{
            installPM()
            after(false,false,false,null)
        }

    }
}
class DummyIAP: IAP {
    override fun getIAPprice(): String {
        return ""
    }

    override fun purchaseIAP(after: (opened: Boolean, purchased: Boolean, err: String?) -> Unit) {
        after(false,false,null)
    }

    override fun restorePurchase(after: (opened: Boolean, restored: Boolean,removedAds:Boolean, err: String?) -> Unit) {
        after(false,false,false,null)
    }

}