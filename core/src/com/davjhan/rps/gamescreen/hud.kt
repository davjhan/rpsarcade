package com.davjhan.rps.gamescreen

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.utils.Align
import com.davjhan.hangdx.Size
import com.davjhan.hangdx.SpriteActor
import com.davjhan.hangdx.spawn
import com.davjhan.rps.*
import ktx.actors.onClick
import ktx.collections.toGdxArray
import net.dermetfan.gdx.graphics.g2d.AnimatedSprite

class Hud(val app: App, val game: GameScreen) : Group() {
    val scoreCounter = hiconAndLabel(app.art.tinyIcons[0], hlabel(app, game.data.score.toString())) {
        pad(Size.sm)
    }

    fun refresh() {
        scoreCounter.label.setText(game.data.score.toString())
        scoreCounter.pack()
        scoreCounter.setPosition(app.disp.screenWidth - Size.sm,
                app.disp.screenHeight - Size.sm, Align.topRight)
    }

    init {
        spawn(scoreCounter)
        spawn(hiconbutton(app,app.art.medIcons[0]) {
            onClick { game.paused = !game.paused }
        }, Size.sm, app.disp.screenHeight - Size.sm, Align.topLeft)
        refresh()
    }

}

fun createVertIconAndText(app: App, icon: TextureRegion, text: String = "Tap!") = htable {
    background = null
    val iconSprite = SpriteActor(icon)
    pad(0f)
    add(iconSprite)
    row()
    add(hlabel(app, text))
    pack()
    touchable = Touchable.disabled
}

fun createTapButon(app: App, text: String = "Tap!") = htable {
    background = null
    pad(0f)
    add(SpriteActor(AnimatedSprite(Animation(0.6f, app.art.hand.slice(0..1).toGdxArray(), Animation.PlayMode.LOOP))))
    row()
    add(hlabel(app, text))
    pack()
    touchable = Touchable.disabled
}

class Sticker(child: Actor, val target: Actor, val targetAlign: Int = Align.bottom,
              val stickerAlign: Int = Align.top, val offsetX: Float = 0f, val offsetY: Float = 0f) : Group() {
    init {
        addActor(child)
        setSize(child.width, child.height)
    }

    override fun setStage(stage: Stage?) {
        super.setStage(stage)
        if (stage != null) {
            stick()
        }
    }

    override fun act(delta: Float) {
        super.act(delta)
        stick()
    }

    private fun stick() {
        setPosition(target.getX(targetAlign) + offsetX, target.getY(targetAlign) + offsetY, stickerAlign)
    }
}