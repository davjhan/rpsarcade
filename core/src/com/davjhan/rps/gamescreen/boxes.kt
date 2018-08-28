package com.davjhan.rps.gamescreen

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.davjhan.hangdx.*
import com.davjhan.rps.App
import com.davjhan.rps.Clock
import com.davjhan.rps.data.ObjType
import java.lang.Math.max
import java.lang.Math.min


class Giftbox(val app: App, val particleGroup: Group, val onOpen: (x: Float, y: Float) -> Character) : GameObject() {
    override val radius: Float = 20f
    val progressBar = Progressbar(app.art.tinyProgressBar,40f,8f)
    val progressSticker = Sticker(progressBar,this,Align.bottom,Align.top,0f,-2f)
    val fullClock = Clock(3f,true){}
    val clock = Clock(1.5f, true) {
        addAction(Actions.sequence(
                Actions.delay(0.5f),
                Actions.parallel(
                        Actions.scaleTo(1.06f,1.06f,0.5f, Interpolation.pow2),
                        acts.sideShake(2f,8)
                ),
                Actions.parallel(
                        Actions.scaleTo(1.15f,1.15f,0.5f, Interpolation.pow3In),
                        acts.blinkWhite(4,this),
                        acts.sideShake(3f,8)
                ),
                Actions.run {
                    val char = onOpen(x, y + 8f)
                    char.addAction(acts.nodReverse())
                    app.sounds.play(app,app.sounds.boxPop)
                    if(app.save.graphicsHigh) throwParticles(app,particleGroup, ParticleType.confetti, 4, getPos(Align.center), width / 2,highPriority = true)
                    remove()
                }
        ))
        app.sounds.play(app,app.sounds.stretch)
    }

    override fun remove(): Boolean {
        progressSticker.remove()
        return super.remove()
    }

    init {
        setSprite(Sprite(app.art.characters[ObjType.BOX][app.save.selectedSkin[ObjType.BOX]]))
        addAction(acts.flashWhite(this,0.15f))
        touchable = Touchable.disabled
    }

    override fun addedToStage() {
        super.addedToStage()

        particleGroup.spawn(progressSticker)
    }
    override fun act(delta: Float) {
        super.act(delta)
        clock.update(delta)
        fullClock.update(delta)
        progressBar.setProgress(1-(fullClock.time/fullClock.resetTime))
    }

}

class KingGiftBox(val app: App, val particleGroup: Group, val onOpen: (x: Float, y: Float) -> Unit) : GameObject() {
    override val radius: Float = 20f

    init {
        setSprite(Sprite(app.art.bigCharacters[ObjType.BOX][app.save.selectedSkin[ObjType.BOX]]))
        addAction(acts.flashWhite(this))
        addAction(Actions.sequence(
                acts.lookAtMe(),
                acts.squashForever()
        ))
        addListener(object : SquishyListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                clearActions()
                return super.touchDown(event, x, y, pointer, button)
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                super.touchUp(event, x, y, pointer, button)
                onOpen(this@KingGiftBox.x, this@KingGiftBox.y)
                if(app.save.graphicsHigh)throwParticles(app,particleGroup, ParticleType.confetti, 12, getPos(Align.center), width,highPriority = true)

                remove()
            }
        })
    }
}
class Progressbar(nines:List<NinePatch>,width:Float,height:Float):SpriteActor(){
    val barSprite = NinepatchSprite(nines[1],width,height)
    init {
        setSprite(nines[0],width,height)
        sprites.add(barSprite)
    }
    fun setProgress(amount:Float){
        val percent =  max(min(amount,1f),0f)
        barSprite.setSize(percent*width,barSprite.height)
    }

}