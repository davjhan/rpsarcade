package com.davjhan.hangdx

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling


/**
 * Created by david on 2018-02-05.
 */
open class GameGroup():Group(){
    var paused:Boolean = false
    override fun act(delta: Float) {
        if(paused) return
        super.act(delta)
    }
}
open class HGroup(init: HGroup.() -> Unit) : Table() {
    open var pWidth = -1f
    open var mWidth = -1f
    var pHeight = -1f

    init {
        init()
        pack()
    }

    override fun getMinWidth(): Float {
        if (mWidth >= 0) return mWidth
        return super.getMinWidth()
    }

    override fun getPrefWidth(): Float {
        if (pWidth >= 0) return pWidth
        return super.getPrefWidth()
    }

    override fun getPrefHeight(): Float {
        if (pHeight >= 0) return pHeight
        return super.getPrefHeight()
    }

    override fun sizeChanged() {
        super.sizeChanged()
        setOrigin(Align.center)
    }

    fun <T : Actor?> addThenRow(actor: T, bottomSpace: Float = Size.reg): Cell<T> {
        val cell = add(actor).spaceBottom(bottomSpace)
        row()
        return cell
    }

    fun fillScreen(disp: Disp) {
        pWidth = disp.screenWidth
        pHeight = disp.screenHeight
    }

    final override fun <T : Actor?> add(actor: T): Cell<T> = super.add(actor)
    open override fun pad(pad: Float): Table = super.pad(pad)
    final override fun pack() = super.pack()
    final override fun setOrigin(alignment: Int) = super.setOrigin(alignment)
    final override fun addListener(listener: EventListener?): Boolean = super.addListener(listener)
    final fun position(align:Int): Vector2 = Vector2(getX(align),getY(align))
}

open class HWrappedLabel(val label: Hlabel, init: HTable.() -> Unit) : HTable({}) {
    init {
        add(label).grow()
        init()
        pad(0f)
    }

    override fun sizeChanged() {
        super.sizeChanged()
        setOrigin(Align.center)
    }

    fun setText(text: String) = label.setText(text)
    fun setAlignment(align: Int) = label.setAlignment(align)
}

interface Tintable{
    var tintColor:Color
    var tintEnabled: Boolean
}
data class Decal(val actor:Actor,
                 val targetAlign:Int,
                 val decalAlign:Int,
                 val xOffset:Float = 0f,
                 val yOffset: Float = 0f)
open class HTable(init: HTable.() -> Unit) : HGroup({}),Tintable {

    override var tintColor:Color = Color(1f,1f,1f,0.6f)
    override var tintEnabled: Boolean = false
    private val decals = mutableListOf<Decal>()
    init {
        pad(Size.reg)
        isTransform = true
        init()
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if(tintEnabled){
            batch.shader.setUniformi("tint_en", 1)
            batch.shader.setUniformf("tint_color", tintColor)
            super.draw(batch, parentAlpha)
            batch.flush()
            batch.shader.setUniformi("tint_en", 0)
        }else{
            super.draw(batch, parentAlpha)
        }

    }

    fun addDecal(actor:Actor,
                 targetAlign:Int,
                 decalAlign:Int,
                 xOffset:Float = 0f,
                 yOffset: Float = 0f){
        decals.add(Decal(actor,targetAlign,decalAlign,xOffset,yOffset))
        spawn(actor, getX(targetAlign)-x+xOffset, getY(targetAlign)-y+yOffset,decalAlign)
    }

    fun removeDecal(actor: Actor)=  decals.removeAll { it.actor == actor }

    override fun sizeChanged() {
        super.sizeChanged()
        setOrigin(Align.center)
        decals.forEach {
            it.actor.setPosition(
                    getX(it.targetAlign)-x+it.xOffset,
                    getY(it.targetAlign)-y+it.yOffset,it.decalAlign)
        }
    }
}

open class HButton(isSquishy: Boolean = true,
                   val naked:Boolean = false,
                   afterinit: HButton.() -> Unit = {}) : HTable({}) {
    var offset: Float = 0f
    var yOffset: Float = 0f
    var onDown:NinePatchDrawable? = null
    var onUp:NinePatchDrawable? = null
    val offsetListener = TouchListener(onDown = {
        background = onDown
        children.forEach { it.moveBy(0f, -yOffset) }
    }, onUp = {
        background = onUp
        children.forEach { it.moveBy(0f, yOffset) }
    })
    init {
        pad(Size.reg)
        touchable = Touchable.enabled
        if (isSquishy) addListener(SquishyListener())

        addListener(offsetListener)
        afterinit()
    }

    fun setBackgrounds(nines: List<NinePatchDrawable>, yOffset: Float = this.yOffset) = setBackgrounds(nines[0],
            nines[1], yOffset)

    fun setBackgrounds(onUp: NinePatch, onDown: NinePatch, yOffset: Float = this.yOffset) = setBackgrounds(NinePatchDrawable(onUp),
            NinePatchDrawable(onDown), yOffset)

    fun contentYOffset(offset: Float) {
        children.forEach { it.moveBy(0f, offset - this.offset) }
        this.offset = offset
    }

    fun setBackgrounds(onUp: NinePatchDrawable, onDown: NinePatchDrawable, yOffset: Float) {
        background = onUp
        this.onDown = onDown
        this.onUp = onUp
        this.yOffset = yOffset
    }


    override fun pad(pad: Float): Table {
        val ret =  super.pad(pad)
        if(!naked)padBottom(pad + 6f)
        return ret
    }

}

open class HInputField(text: String,
                       style: TextField.TextFieldStyle,
                       afterinit: HInputField.() -> Unit = {}) : HButton(true,true, {}) {

    val textfield = HTextField(text, style)

    init {
        pad(Size.reg)
        add(textfield)
        afterinit()
    }

    class HTextField(text: String, style: TextFieldStyle) : TextField(text, style) {
        var allCaps: Boolean = true
        override fun setText(str: String?) {
            super.setText(if (allCaps) str!!.toUpperCase() else str)
        }
    }

    fun text(): String = textfield.text
}

open class HTextButton(text: String,
                       style: Label.LabelStyle,
                       isSquishy: Boolean = true,
                       textStyle: Label.LabelStyle.() -> Unit = {},
                       afterinit: HTextButton.() -> Unit = {}) :
        HButton(isSquishy) {
    val label = Hlabel(text, true, style, textStyle)

    init {
        add(label)
        pad(Size.reg)
        afterinit()
    }
}

open class HImage(draw: Drawable, afterinit: HImage.() -> Unit = {}) : Image(draw) {

    constructor(texReg: TextureRegion, afterinit: HImage.() -> Unit = {})
            : this(TextureRegionDrawable(texReg), afterinit)

    var prefW: Float? = null
    var prefH: Float? = null

    init {
        setScaling(Scaling.fit)
        afterinit()
    }

    override fun getPrefHeight(): Float {
        return if (prefH == null) super.getPrefHeight() else prefH!!
    }

    override fun getPrefWidth(): Float {
        return if (prefW == null) super.getPrefWidth() else prefW!!
    }
}

class HBlendImage(drawable: Drawable, init: HImage.() -> Unit = {}) : HImage(drawable, init) {
    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (batch == null) return
        batch.setBlendFunction(GL20.GL_ZERO, GL20.GL_SRC_COLOR)
        super.draw(batch, parentAlpha)
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
    }
}


class Hlabel(text: String,
             _allcaps: Boolean = true,
             _style: LabelStyle,
             extraStyle: LabelStyle.() -> Unit = {},
             init: Hlabel.() -> Unit = {}) :
        Label(text, _style) {
    var allCaps: Boolean = _allcaps
        set(value) {
            field = value
            if (value) setText(text)
        }

    init {
        touchable = Touchable.enabled
        style.extraStyle()
        setStyle(style)
        allCaps = _allcaps
        setAlignment(Align.center)
        init()
    }

    override fun setText(newText: CharSequence?) {
        super.setText(if (allCaps) newText.toString().toUpperCase() else newText)
    }

    fun setFontColor(color: Color) {
        setStyle {
            fontColor = color
        }
    }

    fun setStyle(init: LabelStyle.() -> Unit = {}) {
        init(style)
        style = style
    }

    fun setFont(_font: BitmapFont) {
        setStyle {
            font = _font
        }
    }
}

open class HStack(init: HStack.() -> Unit) : Stack() {
    init {
        init()
    }

    var prefW: Float? = null
    var prefH: Float? = null
    override fun getPrefHeight(): Float {
        return if (prefH == null) super.getPrefHeight() else prefH!!
    }

    override fun getPrefWidth(): Float {
        return if (prefW == null) super.getPrefWidth() else prefW!!
    }
}

//
class HImageButton(app: HApp,
                   up: Drawable,
                   down: Drawable = up,
                   afterInit: HImageButton.() -> Unit = {}) : HButton() {

    constructor (app: HApp,
                 up: TextureRegion,
                 afterinit: HImageButton.() -> Unit = {}) : this(app, TextureRegionDrawable(up), TextureRegionDrawable(up), afterinit)

    init {

    }
}

class HIconAndLabel(val icon: Actor,
                    val label: Label,
                    pad: Float = Size.sm,
                    val reverseOrder:Boolean = false,
                    afterInit: HIconAndLabel.() -> Unit = {}) : HTable({}) {

    constructor(_icon: TextureRegion,
                label: Label,
                pad: Float = Size.sm,
                reverseOrder:Boolean = false,
                afterInit: HIconAndLabel.() -> Unit = {}) : this(SpriteActor(_icon), label, pad,reverseOrder, afterInit)

    init {
        touchable = Touchable.disabled
        if(reverseOrder){
            add(label)
            add(icon).space(pad)
        }else{
            add(icon).space(pad)
            add(label)
        }
        pad(Size.xs)
        afterInit()
        pack()
    }
}

open class SquishyListener(var enabled: Boolean = true) : InputListener() {

    constructor(target: Group) : this() {
        target.isTransform = true
        target.setOrigin(Align.center)
        target.touchable = Touchable.enabled
    }

    var action: Action? = null
    var touchedDown:Boolean = false
    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        if (!enabled) return false
        if (action != null) {
            event!!.listenerActor.removeAction(action)
        }
        action = Actions.scaleTo(0.95f, 0.95f, 0.1f, Interpolation.pow2)
        event!!.listenerActor.addAction(action)
        event.handle()
        touchedDown = true
        return true
    }

    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
        onExit(event)
        super.touchUp(event, x, y, pointer, button)
    }

    override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
        if(touchedDown)onExit(event)
        super.exit(event, x, y, pointer, toActor)
    }

    fun onExit(event: InputEvent?) {
        if (!enabled) return
        if (action != null) {
            event!!.listenerActor.removeAction(action)
        }
        action = Actions.scaleTo(1f, 1f, 0.1f, Interpolation.pow2)
        event!!.listenerActor.addAction(action)
        touchedDown = false
    }
}

class HScrollPane(actor: Actor) : ScrollPane(actor) {
    var pWidth: Float? = null
    var pHeight: Float? = null
    override fun getPrefHeight(): Float {
        return if (pHeight == null) super.getPrefHeight() else pHeight!!
    }

    override fun getPrefWidth(): Float {
        return if (pWidth == null) super.getPrefWidth() else pWidth!!
    }
    init {
        setupOverscroll(32f, 200f, 300f);
    }
}