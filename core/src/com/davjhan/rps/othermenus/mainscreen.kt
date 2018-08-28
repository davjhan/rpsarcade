package com.davjhan.rps.othermenus

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Align
import com.davjhan.hangdx.*
import com.davjhan.rps.App
import com.davjhan.rps.RatePopup
import com.davjhan.rps.data.ObjType
import com.davjhan.rps.data.Secrets
import com.davjhan.rps.gamescreen.TiledActor
import com.davjhan.rps.hlabel
import com.davjhan.rps.htable
import com.davjhan.rps.othermenus.UiCommon.makePlayButton
import ktx.actors.onClick

class MainScreen(val app: App) : BaseScreen(app.disp) {
    val bg = TiledActor(app.art.bgTile[0], 10, 8)
    val newGameButton = makePlayButton(app,this)
    var easterEggcounter = 0
    val version = hlabel(app, "v1.6")
    val title = HImage(app.art.title) {
        onClick {
            easterEggcounter++
            if (easterEggcounter >= 100) {
                val secret = Secrets.byId(ObjType.BOX, 1)
                if (secret.unlock(app)) {
                    Secrets.onSecretUnlocked(app, this@MainScreen, secret)
                }
            }
        }
        addListener(SquishyListener())
    }
    val secretsButton = UiCommon.makeSecretsButton(app) { app.controller.changeScreenTo(this) }
    val removeAdsButton = UiCommon.makeRemoveAdsButton(app,this)
    override fun onBackPressed() {
        Gdx.app.exit()
    }

    init {
        stage.spawn(bg, disp.position(Align.center), Align.center)
        stage.spawn(version, disp.position(Align.bottomRight, Size.sm), Align.bottomRight)
        stage.spawn(title, disp.position(Align.center).add(0f, 96f), Align.center)
        stage.spawn(htable {
            addThenRow(newGameButton).fillX()
            addThenRow(secretsButton).fillX().spaceBottom(Size.sm)
            if(!app.save.removeAds) addThenRow(removeAdsButton).fillX()
            top()
        }, title.getX(Align.bottom),title.getY(Align.bottom), Align.top)
        stage.spawn(UiCommon.makeSettingsButton(app, this), disp.position(Align.topRight, Size.reg), Align.topRight)
        title.setOrigin(Align.center)
        title.addAction(acts.lookAtMe())
        if (!app.save.seenRatePopup && app.save.gamesPlayed > 10) {
            val ratePopup = RatePopup(app)
            showOverlay(ratePopup)
        }
    }
}