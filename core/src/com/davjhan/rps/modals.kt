package com.davjhan.rps

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.davjhan.hangdx.*
import com.davjhan.rps.othermenus.UiCommon.makeCancelButton
import com.davjhan.rps.othermenus.UiCommon.makeConfirmButton
import ktx.actors.onClick

open class Popup(val app: App) : Group(), BaseOverlay {
    override val actor: Actor get() = this
    val bg = SpriteActor(pixUtils.solidRect(colr.superdim,
            app.disp.screenWidth.toInt(),
            app.disp.screenHeight.toInt()))
    val body = htable() {
        background = app.art.bigNines.paper
        pad(Size.xreg)
        pWidth = app.disp.screenWidth - (Size.xreg * 2)
        touchable = Touchable.enabled
    }

    init {
        spawn(bg)
        spawn(body)
        bg.onClick {
            cancel()
        }
    }

    fun pack() {
        body.setPosition(app.disp.hScreenWidth, app.disp.hScreenHeight, Align.center)
    }

    open fun cancel() {
        remove()
    }

    override fun onBackPressed() {
        cancel()
    }

}

class ConfirmPopup(app: App, bodyText: String,
                   val onCancel: () -> Unit = {},
                   val onConfirm: () -> Unit) : Popup(app) {

    init {
        body.add(hlabel(app, bodyText, app.font.reg1) {
            setWrap(true)
            setAlignment(Align.top)
        }).growX().expandY().space(Size.lg).colspan(2)
        body.row()
        body.add(makeCancelButton(app) { cancel() }).grow().space(Size.sm)
        body.add(makeConfirmButton(app) {
            remove()
            onConfirm()
        }).grow()
        body.pack()
        pack()

    }

    override fun cancel() {
        super.cancel()
        onCancel()
    }

}


class ErrorPopup(app: App, bodyText: String) : Popup(app) {

    init {
        body.addThenRow(hlabel(app, "Error", app.font.red1) {
            setWrap(true)
            setAlignment(Align.center)
        }).grow().space(Size.lg)
        body.add(hlabel(app, bodyText, app.font.reg1) {
            setWrap(true)
            setAlignment(Align.top)
        }).growX().expandY().space(Size.xl)
        body.row()
        body.add(makeConfirmButton(app,"Okay") {
            cancel()

        }).grow()
        body.pack()
        pack()

    }

}
class LoadingPopup(app: App,titleText:String = "Loading...",val after:()->Unit = {}) : Popup(app) {

    init {
        body.addThenRow(hlabel(app, titleText) {
            setWrap(true)
            setAlignment(Align.center)
        }).grow()
        body.pad(Size.lg)
        body.pack()
        pack()

    }

    override fun cancel() {
        super.cancel()
        after()
    }
}
class SuccessPopup(app: App, bodyText: String,titleText:String = "Success",val after:()->Unit = {}) : Popup(app) {

    init {
        body.addThenRow(hlabel(app, titleText, app.font.green1) {
            setWrap(true)
            setAlignment(Align.center)
        }).grow().space(Size.lg)
        body.add(hlabel(app, bodyText, app.font.reg1) {
            setWrap(true)
            setAlignment(Align.top)
        }).growX().expandY().space(Size.xl)
        body.row()
        body.add(makeConfirmButton(app,"Okay") {
            cancel()

        }).grow()
        body.pack()
        pack()

    }

    override fun cancel() {
        super.cancel()
        after()
    }
}
class RatePopup(app: App):Popup(app) {

    init {
        body.addThenRow(hlabel(app, "Enjoying the game?", app.font.reg1) {
            setWrap(true)
            setAlignment(Align.top)
        }).growX().expandY()
        body.row()
        body.addThenRow(htable{
            for(i in 0..4){
                val star = SpriteActor(app.art.largeIcons[9])
                star.addAction(Actions.delay(i*0.1f,Actions.forever(
                        Actions.sequence(acts.lookAtMeShine(star), Actions.delay(1f))
                )))
                add(star).space(Size.reg)
            }
        }).grow()
        body.addThenRow(hlabel(app, "Rating the game helps others discover it in the App store.", app.font.reg1) {
            setWrap(true)
            setAlignment(Align.top)
        }).grow().spaceBottom(Size.lg)

        val rateButton = makeConfirmButton(app,"Yes! Lets go rate it!") {
            app.save.changeValues {
                it.seenRatePopup = true
            }
            app.bridge.openRateLink()
            remove()
        }
        body.addThenRow(rateButton).space(Size.sm).height(96f).grow()
        body.add(makeCancelButton(app,"Don't show this again.") {
            app.save.changeValues {
                it.seenRatePopup = true
            }
            remove()
        }).grow()
        body.pack()
        pack()
        rateButton.addAction(Actions.forever(acts.lookAtMe(0.4f,1.05f)))
    }

    override fun cancel() {

    }

}