package com.davjhan.rps.data

import ktx.log.info

interface Bridge{
    fun reportAnalytic(category:String,
                       action:String,
                       label:String?=null,
                       value:Int?=null)


    fun showAd(after:(didShow:Boolean)->Unit)
    fun loadAd()
    fun openRateLink()

}
object Analytics{
    object Category{
        const val gameplay = "gameplay"
        const val secrets = "secrets"
        const val ads = "ads"
    }
    object Action{
        const val adShown = "adShown"
        const val tutorialPassed = "tutorialPassed"
        const val newGame = "newGame"
        const val newSession = "newSession"
        const val highScore = "highScore"
        const val gameOver = "gameOver"
        const val secretUnlocked = "secretUnlocked"
    }
}
object Ads{
    fun shouldLoadAd(save: Save):Boolean{
        info ("tttt [Ads]"){"Should Load Ad: played:${save.gamesPlayed} sinceLast:${save.gamesSinceLastAd}"}
        if(!save.removeAds && save.gamesPlayed > 3){
            if(save.gamesPlayed >= 25){
                return  save.gamesSinceLastAd >= 2
            }else {
                return  save.gamesSinceLastAd >= 3
            }
        }
        return false
    }
}
