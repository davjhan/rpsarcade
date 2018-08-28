package com.davjhan.rps.othermenus

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.davjhan.hangdx.*
import com.davjhan.rps.*
import com.davjhan.rps.data.Ads
import com.davjhan.rps.data.ObjType
import com.davjhan.rps.data.Secret
import com.davjhan.rps.data.Secrets
import com.davjhan.rps.gamescreen.ParticleType
import com.davjhan.rps.gamescreen.throwParticles
import com.davjhan.rps.secretscreen.SecretsScreen
import ktx.actors.onClick
import ktx.log.info

object UiCommon {
    fun makePlayButton(app: App,currScreen:BaseScreen) = hbutton(app) {
        val icon = app.art.flags
        val iconOffset = 20f
        val yOffset = -24f

        addDecal(HImage(TextureRegion(icon).apply { flip(true, false) }), Align.top, Align.center, iconOffset, yOffset)
        addDecal(HImage(icon), Align.top, Align.center, -iconOffset, yOffset)
//        addDecal(HImage(icon),Align.top, Align.center,0f,yOffset)
        row()
        add(hlabel(app, "New Game", app.font.yellow1) {
            pad(32f)
            padTop(36f)
            padBottom(24f)
        })
        setBackgrounds(app.art.bg.yellowButton)

        fun showGameScreen() {
            Gdx.app.postRunnable {
                if (app.save.gameFirstTime) {
                    app.controller.changeScreenTo(TutorialScreen(app))

                } else {
                    app.controller.changeScreenTo(GameScreen(app))
                }
            }
        }
        onClick {
            if (Ads.shouldLoadAd(app.save)) {
                app.bridge.showAd { didShow ->
                    if (didShow) {
                        app.save.changeValues {
                            it.gamesSinceLastAd = 0
                        }
                    }
                    showGameScreen()
                }
            } else {
                showGameScreen()
            }

        }

        addAction(acts.lookAtMeShine(this))
        addAction(Actions.after(
                Actions.forever(acts.lookAtMe(0.3f, 1.07f))
        ))

    }

    fun makeRemoveAdsButton(app: App, screen: BaseScreen) = htextAndIconButton(app, "Remove Ads", app.art.medIcons[1]) {
        setBackgrounds(app.art.bg.darkBlueButton)
        onClick {
            var dissmissed = false
            val loading = LoadingPopup(app, "Connecting to the app store...") {
                dissmissed = true
            }
            screen.showOverlay(loading)

            app.iap.purchaseIAP { opened, purchased, err ->
                info ("tttt [UiCommon]"){"purcahse iap $opened $purchased $err dissmissed $dissmissed"}
                if(dissmissed) Unit
                loading.remove()
                if (opened) {
                    if (purchased) {
                        app.save.changeValues {
                            it.removeAds = purchased
                        }
                        screen.showOverlay(SuccessPopup(app, "Thank you for your purchase!") {
                            app.controller.changeScreenTo(MainScreen(app))
                        })

                        val secret = Secrets.byId(ObjType.PAPER, 5)
                        if (secret.unlock(app)) {
                            Secrets.onSecretUnlocked(app, screen, secret)
                        }

                    } else {
                        if (err != null) {
                            screen.showOverlay(ErrorPopup(app, err))
                        }
                    }
                } else {
                    screen.showOverlay(ErrorPopup(app, "Could not connect to the app store. Please check your Network Connection."))
                }
            }
        }
    }

    fun makeIconButton(app: App, icon: TextureRegion, text: String,
                       font: BitmapFont = app.font.reg1,
                       init: HButton.() -> Unit = {}) = hbutton(app) {
        add(HImage(icon))
        addDecal(hlabel(app, text, font), Align.center, Align.top, 0f, -4f)
        padTop(Size.reg)
        padBottom(Size.xl)
        init()
    }

    fun makeCancelButton(app: App, text: String = "Cancel", onClick: () -> Unit) = makeIconButton(app, app.art.largeIcons[4], text,
            app.font.red1) {

        setBackgrounds(app.art.bg.redButton)
        onClick(onClick)
    }

    fun makeConfirmButton(app: App, text: String = "Confirm", onClick: () -> Unit) = makeIconButton(app, app.art.largeIcons[5], text,
            app.font.green1) {
        setBackgrounds(app.art.bg.greenButton)
        onClick(onClick)
    }

    fun makeSecretsButton(app: App, onBack: () -> Unit) = hbutton(app) {
        val yOffset = -16f
        val icon = HImage(app.art.secretIcons[0])
        icon.setOrigin(Align.center)
        addDecal(icon, Align.top, Align.center, 0f, yOffset)
        val badge = HImage(app.art.newBadge)

        if (app.save.hasNewSecrets) {
            badge.addAction(Actions.forever(acts.lookAtMe(0.3f, 1.05f)))
            icon.addAction(Actions.forever(acts.lookAtMe(0.3f, 1.05f)))
            addDecal(badge, Align.topRight, Align.topRight, 4f, 4f)
        }
        add(hlabel(app, "Secrets", app.font.reg1) {
            pad(32f)
            padTop(32f)
            padBottom(16f)
        })
        onClick {
            app.controller.changeScreenTo(SecretsScreen(app, onBack))

        }

    }

    fun makeSettingsButton(app: App, screen: BaseScreen, parent: Group? = null) = hiconbutton(app,app.art.largeIcons[1]) {


        onClick {
            val settings = SettingsOverlay(app, screen, {})
            screen.showOverlay(settings, false)
            parent?.spawn(settings) ?: stage.spawn(settings)
        }
    }

    fun makeSecretIcon(app: App, secret: Secret) =
            htable {
                val icon = HImage(if (secret.isLocked(app.save)) app.art.secretIcons[0] else secret.icon(app))
                pad(0f)
                if (secret.isLocked(app.save)) {
                    background = app.art.bigNines.griditem
                } else {
                    if (!secret.isAcked(app.save)) addDecal(HImage(app.art.newBadge), Align.topRight, Align.topRight, 4f, 4f)
                    if (secret.isEnabled(app.save)) background = app.art.bigNines.gridItemGold
                    else background = app.art.bigNines.griditemWhite
                    val typeIcon = HImage(app.art.tinyIcons[4 + secret.type])
                    addDecal(typeIcon, Align.topLeft, Align.topLeft, Size.sm, -Size.sm)
                }


                add(icon)
                pack()
            }

    fun makeSecretUnlockedNotification(app: App, secret: Secret, onExit: () -> Unit) =
            htable {
                pWidth = app.disp.screenWidth
                pad(Size.reg)
                background = TextureRegionDrawable(pixUtils.linearDim(colr.dim, 1, 120))
                val body = htable {
                    touchable = Touchable.enabled
//                    padTop(Size.xreg)
                    val icon = makeSecretIcon(app, secret)
                    icon.touchable = Touchable.disabled
                    background = app.art.bigNines.paper


                    add(icon).width(80f).height(80f).expandY().space(Size.sm)
                    add(hlabel(app, secret.description) {
                        touchable = Touchable.disabled
                        setWrap(true)
                        setAlignment(Align.topLeft)
                    }).growX().space(Size.sm)
//                    add(HImage(app.art.largeIcons[2]) { touchable = Touchable.disabled }).space(Size.sm)

                    val title = hlabel(app, "Secret Unlocked", app.font.sub1)
                    addDecal(title, Align.top, Align.center, 30f, -4f)

                    title.addAction(acts.lookAtMe())


                    addListener(SquishyListener())
                }
                body.onClick {
                    this@htable.remove()
                    app.controller.changeScreenTo(SecretsScreen(app, onExit, secret))
                }
                add(body).grow()
                pack()

                throwParticles(app,this, ParticleType.confetti, 30, position(Align.top), app.disp.hScreenWidth, 32f)
                addAction(Actions.sequence(
                        Actions.moveBy(0f, -height),
                        Actions.moveBy(0f, height, 0.7f, Interpolation.pow2Out),
                        Actions.delay(6f),
                        Actions.moveBy(0f, -height, 0.7f, Interpolation.pow2Out),
                        Actions.run { this@htable.remove() }
                ))
                addDecal(hiconbutton(app,app.art.largeIcons[7]) {
                    onClick {
                        body.clearActions()
                        this@htable.addAction(Actions.sequence(
                                Actions.moveBy(0f, -height, 0.1f, Interpolation.pow2Out),
                                Actions.run { this@htable.remove() }
                        ))
                    }
                    touchable = Touchable.enabled
                }, Align.topRight, Align.center, -12f, -16f)

            }

}
