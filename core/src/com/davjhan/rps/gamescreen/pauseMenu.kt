package com.davjhan.rps.gamescreen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.davjhan.hangdx.*
import com.davjhan.rps.*
import com.davjhan.rps.othermenus.MainScreen
import com.davjhan.rps.othermenus.TutorialScreen
import com.davjhan.rps.othermenus.UiCommon
import com.davjhan.rps.othermenus.UiCommon.makeIconButton
import ktx.actors.onClick

class PauseOverlay(val app: App,screen: BaseScreen, val onResume: () -> Unit) : HTable({}),BaseOverlay {
    override val actor: Actor get() = this
    var help = hlabel(app, "Paused", app.font.reg1) {
        pad(Size.sm)
        setWrap(true)
        setAlignment(Align.center)
    }
    val bg = PanningTiledActor(app.art.bgTile[3], 11, 6, 4f)
    val settingsButton = UiCommon.makeSettingsButton(app,screen, this)

    init {
        fillScreen(app.disp)
        addActor(bg)
        center()
        addThenRow(help).padTop(Size.xl).spaceBottom(Size.lg).growX().center()
        addThenRow(makeIconButton(app, app.art.largeIcons[5], "Resume", app.font.yellow1) {
            setBackgrounds(app.art.bg.yellowButton)
            onClick {
                this@PauseOverlay.remove()
                onResume()
            }
        }).width(120f)
        addThenRow(htextbutton(app, "Restart") {

            onClick {
                val confirmModal = ConfirmPopup(app, "Are you sure you want to restart the game?") {
                    app.controller.changeScreenTo(GameScreen(app))
                }
                screen.showOverlay(confirmModal)
            }
        }).width(120f).spaceBottom(Size.sm)
        addThenRow(htextbutton(app, "Quit") {
            onClick {
                val confirmModal = ConfirmPopup(app, "Are you sure you want to Quit?") {
                    app.controller.changeScreenTo(MainScreen(app))
                }
                screen.showOverlay(confirmModal)
            }
        }).width(120f).spaceBottom(Size.sm)
        addThenRow(htextbutton(app, "How to Play") {
            onClick {
                val confirmModal = ConfirmPopup(app, "This will end your current game. Are you ready?") {
                    app.controller.changeScreenTo(TutorialScreen(app))
                }
                screen.showOverlay(confirmModal)


            }
            setBackgrounds(app.art.bg.darkBlueButton)
        }).width(120f)

        pack()
        spawn(settingsButton, app.disp.position(Align.topRight, Size.reg), Align.topRight)


    }

    override fun onBackPressed() {
        this@PauseOverlay.remove()
        onResume()
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)){
            remove()
            onResume()
        }
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
