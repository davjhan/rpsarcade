package com.davjhan.rps.othermenus

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.davjhan.hangdx.*
import com.davjhan.rps.*
import com.davjhan.rps.gamescreen.PanningTiledActor
import ktx.actors.onClick


class SettingsOverlay(val app: App,screen:BaseScreen, val onResume: () -> Unit) : HTable({}),BaseOverlay {
    override val actor = this
    var help = hlabel(app, "Settings", app.font.reg1) {
        pad(Size.sm)
        setWrap(true)
        setAlignment(Align.center)
    }
    val backButton = hiconbutton(app,app.art.largeIcons[0]) {
        onClick {
            this@SettingsOverlay.remove()
            onResume()
        }
    }
    val bg = PanningTiledActor(app.art.bgTile[3], 11, 6, 4f)

    init {
        addActor(bg)
        fillScreen(app.disp)
        center()
        add(htable {
            addThenRow(help).padTop(Size.xl).spaceBottom(Size.lg).growX().center()
            addThenRow(htable {
                pad(0f)
                add(hbutton(app){
                    add(SpriteActor(if(app.save.soundOn)app.art.largeIcons[5] else app.art.largeIcons[4]))
                    row()
                    add(hlabel(app,"Sounds"))
                    setBackgrounds(if(app.save.soundOn)app.art.bg.greenButton else app.art.bg.redButton)

                    onClick {
                        app.save.changeValues {
                            it.soundOn = !it.soundOn
                        }
                        cells[0].setActor<SpriteActor>(SpriteActor(if(app.save.soundOn)app.art.largeIcons[5] else app.art.largeIcons[4]))
                        setBackgrounds(if(app.save.soundOn)app.art.bg.greenButton else app.art.bg.redButton)
                    }
                }).spaceRight(Size.sm).growX()
                if(Gdx.app.type == Application.ApplicationType.Android){
                    add(hbutton(app){
                        add(SpriteActor(if(app.save.vibrateOn)app.art.largeIcons[5] else app.art.largeIcons[4]))
                        row()
                        add(hlabel(app,"Vibrate"))
                        setBackgrounds(if(app.save.vibrateOn)app.art.bg.greenButton else app.art.bg.redButton)

                        onClick {
                            app.save.changeValues {
                                it.vibrateOn = !it.vibrateOn
                            }
                            cells[0].setActor<SpriteActor>(SpriteActor(if(app.save.vibrateOn)app.art.largeIcons[5] else app.art.largeIcons[4]))
                            setBackgrounds(if(app.save.vibrateOn)app.art.bg.greenButton else app.art.bg.redButton)
                            if(app.save.vibrateOn) Vibrate.short(app)
                        }
                    }).spaceRight(Size.sm).growX()
                }
            }).spaceBottom(Size.sm).fillX()
            addThenRow(htextbutton(app, "Graphics: ${if(app.save.graphicsHigh)"high" else "low"}") {
                onClick {
                    app.save.changeValues {
                        it.graphicsHigh = !it.graphicsHigh
                        label.setText("Graphics: ${if(it.graphicsHigh)"high" else "low"}")
                    }
                }

            }).fillX().spaceBottom(Size.lg)
            addThenRow(htextbutton(app, "Reset Saved Data") {
                setBackgrounds(app.art.bg.darkBlueButton)
                onClick {
                    val confirmModal = ConfirmPopup(app, "This will reset all progress. This action is not undoable.") {
                        app.resetSaves()
                        app.controller.changeScreenTo(MainScreen(app))
                    }
                    screen.showOverlay(confirmModal)
                }
            }).fillX().spaceBottom(Size.sm)
            addThenRow(htextbutton(app, "Restore Purchases") {
                setBackgrounds(app.art.bg.darkBlueButton)
                onClick {
                    app.iap.restorePurchase{
                        opened, restored, removedAds, err ->
                        if(!opened){
                            screen.showOverlay(ErrorPopup(app,"Could not connect to the app store. Please check your Network Connection."))
                        }else{
                            if(restored){
                                if(removedAds){
                                    screen.showOverlay(SuccessPopup(app,"Your purchases have been restored. Ads have been removed."))
                                }
                                screen.showOverlay(SuccessPopup(app,"You have no purchases to restore"))
                                app.save.changeValues {
                                    it.removeAds = removedAds
                                }
                            }else{
                                screen.showOverlay(ErrorPopup(app,"Could not restore purchases. $err"))
                            }
                        }
                    }
                }
            }).fillX()

            addThenRow(hlabel(app, "This game was designed, drawn, and developed by David Han (@davjhan).",
                    app.font.sub1) {
                setWrap(true)
            }).fillX()
            addThenRow(hlabel(app, "Special thanks to Kevin Tieu (@catfish) for the sound design.",
                    app.font.sub1) {
                setWrap(true)
            }).fillX()

        })


        pack()
        spawn(backButton, app.disp.position(Align.topLeft, Size.reg), Align.topLeft)

    }

    override fun onBackPressed() {
        this@SettingsOverlay.remove()
        onResume()
    }

    override fun setParent(parent: Group?) {
        super.setParent(parent)
        if (parent != null) onShow()
    }

    fun onShow() {
        addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.fadeIn(0.3f)
        ))

    }
}
