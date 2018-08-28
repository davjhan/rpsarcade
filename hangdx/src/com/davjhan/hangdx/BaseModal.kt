package com.davjhan.hangdx

import com.badlogic.gdx.Gdx.app
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import ktx.actors.alpha
import ktx.actors.onClick
import ktx.actors.plus
import ktx.log.info

/**
 * Created by david on 2018-01-08.
 */
abstract class BaseModal(val app: HApp,
                         dimColor: Color = hColor.dim) : WidgetGroup() {
    val bgTex = pixUtils.solidRect(dimColor, 1, 1)
    val background: Image = HImage(TextureRegionDrawable(bgTex)) {
        setFillParent(true)
        setScaling(Scaling.stretch)
        touchable = Touchable.enabled
        onClick { close() }
    }

    class MainTable(app: HApp, afterinit: HTable.() -> Unit) : HTable(afterinit) {
        override var pWidth: Float =  app.disp.screenWidth - (Size.lg * 2)
    }

    val main: HTable = MainTable(app) {
        //        background = app.art.bgRound.ink
        isTransform = true
        pad(Size.reg)
    }

    init {

        this + background
        this + main
        setSize(app.disp.screenWidth, app.disp.screenHeight)

    }

    override fun setStage(stage: Stage?) {
        super.setStage(stage)
        if (stage != null) {
            onShow()
        }
    }

    open fun onShow() {
        background.alpha = 0f
        background.addAction(Actions.fadeIn(0.2f, Interpolation.pow2Out))
        main.addAction(acts.lookAtMe())
    }

    final override fun setSize(width: Float, height: Float) {
        super.setSize(width, height)
    }

    open fun close() {
        addAction(Actions.sequence(Actions.fadeOut(0.2f, Interpolation.pow2Out),
                Actions.run { closeFinal() }))
    }

    override fun remove(): Boolean {
        clearActions()
        alpha = 1f
        background.clearActions()
        main.clearActions()
        return super.remove()
    }

    open fun closeFinal() {
        remove()
    }
}