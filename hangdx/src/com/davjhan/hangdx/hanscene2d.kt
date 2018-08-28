package com.davjhan.hangdx

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Align.*
import net.dermetfan.gdx.graphics.g2d.AnimatedSprite


/**
 * Created by david on 2017-12-23.
 */

//
fun Stage.spawn(actor: Actor) {
    addActor(actor)
}
fun Stage.spawn(actor: Actor, vec:Vector2, align: Int = Align.bottomLeft) {
    actor.setPosition(vec.x, vec.y, align)
    addActor(actor)
}
fun Stage.spawn(actor: Actor, x: Float, y: Float, align: Int = Align.bottomLeft) {
    actor.setPosition(x, y, align)
    addActor(actor)
}

fun Group.spawn(actor: Actor, pos: Vector2, align: Int = Align.bottomLeft) {
    actor.setPosition(pos.x, pos.y, align)
    addActor(actor)
}

fun Group.spawn(actor: Actor, x: Float, y: Float, align: Int = Align.bottomLeft) {
    actor.setPosition(x, y, align)
    addActor(actor)
}

fun Group.spawn(actor: Actor) {
    addActor(actor)
}


fun Actor.moveBy(vec: Vector2) {
    moveBy(vec.x, vec.y)
}

fun AnimatedSprite.reset(){
    time = 0f
    play()
    isAutoUpdate = true
}

fun Sprite.getY(alignment: Int): Float {
    var y = this.y
    if (alignment and top != 0)
        y += height
    else if (alignment and bottom == 0)
    //
        y += height / 2
    return y
}

fun Sprite.getX(alignment: Int): Float {
    var x = this.x
    if (alignment and right != 0)
        x += width
    else if (alignment and left == 0)
    //
        x += width / 2
    return x
}

fun Sprite.setPosition(x: Float, y: Float, alignment: Int) {
    var x = x
    var y = y
    if (alignment and right != 0)
        x -= width
    else if (alignment and left == 0)
    //
        x -= width / 2

    if (alignment and top != 0)
        y -= height
    else if (alignment and bottom == 0)
    //
        y -= height / 2

    if (this.x != x || this.y != y) {
        this.x = x
        this.y = y
    }
}

class TouchListener(val onDown: () -> Unit, val onUp: () -> Unit) : InputListener() {
    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        if (pointer == 0) onDown()
        return true
    }

    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
        if (pointer == 0) onUp()
        super.touchUp(event, x, y, pointer, button)
    }

}
class NinepatchSprite(val nine:NinePatch,width:Float,height:Float): Sprite() {
    init {
        setSize(width,height)
    }
    override fun draw(batch: Batch?) {
        nine.draw(batch,x,y,originX,originY,width,height,scaleX,scaleY,rotation)
    }
}

