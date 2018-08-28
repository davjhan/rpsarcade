package com.davjhan.rps

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.davjhan.hangdx.*

/**
 * Created by david on 2018-02-05.
 */
fun hlabel(app: App, text: String,
           _font: BitmapFont = app.font.reg1,
           _allcaps: Boolean = true,
           init: Hlabel.() -> Unit = {}):
        Hlabel = Hlabel(text, _allcaps, Label.LabelStyle(_font, null), {}, init)

fun hbutton(app: App,
            isSquishy: Boolean = false,
            naked: Boolean = false,
            afterinit: HButton.() -> Unit = {}):
        HButton = HButton(isSquishy, naked) {

    if (!naked) {
        setBackgrounds(app.art.bg.primaryButtion, 2f)
        pad(Size.xreg)
    }

    addListener(ButtonSoundListener(app, app.sounds.buttonDown, app.sounds.buttonUp))
    afterinit()
    pack()
}

class ButtonSoundListener(val app: App, val down: Sound, val up: Sound) : InputListener() {
    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        app.sounds.play(app, down)
        return true
    }

    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
        app.sounds.play(app, up)
        super.touchUp(event, x, y, pointer, button)
    }
}

fun hiconbutton(app:App,icon: TextureRegion,
                afterinit: HButton.() -> Unit = {}):
        HButton = HButton(true) {
    pad(Size.sm)
    add(HImage(icon))
    afterinit()
    pack()
    addListener(ButtonSoundListener(app,app.sounds.buttonDownPop,app.sounds.buttonUpPop))

}

fun hinputfield(app: App, text: String,
                font: BitmapFont = app.font.reg1,
                afterinit: HInputField.() -> Unit = {}):
        HInputField = HInputField(text,
        TextField.TextFieldStyle(font, Color.WHITE,
                pixUtils.solidRectDrawable(colr.ink),
                pixUtils.solidRectDrawable(colr.dim),
                null)) {
    //    background = app.art.bg.outlineInk
    afterinit()
}

fun htextbutton(app: App,
                text: String,
                isSquishy: Boolean = false,
                labelstyle: Label.LabelStyle.() -> Unit = {},
                afterinit: HTextButton.() -> Unit = {}):
        HTextButton = HTextButton(text, Label.LabelStyle(app.font.reg1, null), isSquishy,
        textStyle = labelstyle) {
    setBackgrounds(app.art.bg.primaryButtion, 2f)
    pad(Size.xreg)
    padBottom(Size.lg)
    afterinit()
    pack()
    addListener(ButtonSoundListener(app,app.sounds.buttonDown,app.sounds.buttonUp))
}

fun htextAndIconButton(app: App, text: String, icon: TextureRegion,
                       afterinit: HButton.() -> Unit = {}): HTable =
        hbutton(app) {
            val body = hiconAndLabel(icon, hlabel(app, text))
            add(body)
            pad(Size.reg)
            padBottom(Size.xreg)
            pack()
            afterinit()
            addListener(ButtonSoundListener(app,app.sounds.buttonDown,app.sounds.buttonUp))
        }

fun htable(init: HTable.() -> Unit = {}): HTable = HTable {
    init()
    pack()
}

fun hscrollpane(actor: Actor, init: HScrollPane.() -> Unit = {}): HScrollPane {
    val scrollpane = HScrollPane(actor)
    scrollpane.init()
    return scrollpane
}

fun wrappedLabel(app: App,
                 label: Hlabel,
                 init: HTable.() -> Unit = {}): HWrappedLabel = HWrappedLabel(label, init)

fun hiconAndLabel(_icon: TextureRegion, label: Label, pad: Float = 4f,
                  reverseOrder: Boolean = false,
                  init: HIconAndLabel.() -> Unit = {}) = HIconAndLabel(_icon, label, pad, reverseOrder, init)

fun hiconAndLabel(_icon: Actor, label: Label, pad: Float = 4f,
                  reverseOrder: Boolean = false,
                  init: HIconAndLabel.() -> Unit = {}) = HIconAndLabel(_icon, label, pad, reverseOrder, init)

fun hgroup(init: HGroup.() -> Unit = {}): HGroup = HGroup {
    init()
}