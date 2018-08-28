package com.davjhan.hangdx

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions.action
import com.badlogic.gdx.utils.Align

/**
 * Created by david on 2018-02-05.
 */
open class HAct {
    val tempOrigin = Vector2()
    val temp1 = Vector2()
    fun lookAtMe(duration: Float = 0.15f, scaleUp: Float = 1.1f): Action =
            Actions.sequence(
                    Actions.scaleTo(scaleUp, scaleUp, duration, Interpolation.pow2Out),
                    Actions.scaleTo(1-(1-scaleUp)/2, 1-(1-scaleUp)/2, duration/2, Interpolation.pow2In),
                    Actions.scaleTo(1f, 1f, duration/2, Interpolation.pow2Out)
                    )

    fun lookAtMeShine(actor:Tintable,duration: Float = 0.15f, scaleUp: Float = 1.1f): Action =
            Actions.parallel(
                    Actions.delay(duration/2,flashWhite(actor)),
                    lookAtMe(duration,scaleUp)
            )

    fun squashForever(duration: Float = 0.7f, scale: Float = 1.05f): RepeatAction {
        return Actions.forever(
                squashAndStretch(duration)
        )
    }

    fun hShake(actor: Actor, intensity: Float, count: Int): Action {
        val seq = Actions.sequence()
        for (i in 0 until count) {
            val dire = 1 - 2 * (i % 2)
            seq.addAction(Actions.moveTo(actor.x + dire * intensity, actor.y, 0.04f))
        }
        seq.addAction(Actions.moveTo(actor.x, actor.y, 0.04f))
        return seq
    }

    fun bob(duration: Float = 0.6f, amount: Float = 5f): Action {
        return Actions.forever(
                Actions.sequence(
                        Actions.moveBy(0f, amount, duration, Interpolation.pow2),
                        Actions.moveBy(0f, -amount, duration, Interpolation.pow2)
                )
        )
    }

    fun flashWhite(actor: Tintable, duration: Float = 0.075f): Action =
            Actions.sequence(
                    Actions.run { actor.tintEnabled = true },
                    Actions.delay(duration),
                    Actions.run { actor.tintEnabled = false }
            )

    fun blinkWhite(count: Int, actor: Tintable): Action =
            Actions.repeat(count, Actions.sequence(
                    flashWhite(actor),
                    Actions.delay(0.075f)
            ))


    fun nod(): SequenceAction {
        return Actions.sequence(
                Actions.moveBy(0f, -8f, 0.05f, Interpolation.pow2Out),
                Actions.moveBy(0f, 8f, 0.05f, Interpolation.pow2Out)
        )
    }

    fun nodReverse(dist: Float = 8f): SequenceAction {
        return Actions.sequence(
                Actions.moveBy(0f, dist, 0.05f, Interpolation.pow2Out),
                Actions.moveBy(0f, -dist, 0.05f, Interpolation.pow2In)
        )
    }

    fun hop(duration: Float = 0.2f, down: Float = 1.05f, up: Float = 1f, height: Float = 4f): Action {
        return Actions.parallel(Actions.sequence(
                Actions.moveBy(0f, height, duration, Interpolation.pow2Out),
                Actions.moveBy(0f, -height, duration, Interpolation.pow2In)
        ),
                Actions.sequence(
                        Actions.scaleTo(2f - up, up, duration, Interpolation.pow2Out),
                        Actions.scaleTo(down, 2f - down, duration, Interpolation.pow2In)
                ))
    }

    fun squashAndStretch(duration: Float = 0.4f, down: Float = 1.05f, up: Float = 1f): SequenceAction {
        return Actions.sequence(
                Actions.scaleTo(down, 2f - down, duration, Interpolation.circle),
                Actions.scaleTo(2f - up, up, duration, Interpolation.circle)
        )
    }

    fun sideShake(amount: Float, count: Int): Action {
        val seq = Actions.sequence()
        tempOrigin.set(0f, 0f)
        for (i in 0 until count) {
            temp1.set(if (i % 2 == 0) -amount else amount, 0f)
            tempOrigin.add(temp1)
            seq.addAction(Actions.moveBy(temp1.x, temp1.y, 0.05f))
        }
        seq.addAction(Actions.moveBy(temp1.x, temp1.y, 0.05f))

        return seq
    }

    fun shake(_intensity: Float, decay: Float = 1f): Action {
        var intensity = _intensity
        val seq = Actions.sequence()
        tempOrigin.set(0f, 0f)
        while (intensity > 0) {
            temp1.set(0f, intensity)
            temp1.setAngle(tempOrigin.angle() + MathUtils.random(90, 270))
            tempOrigin.add(temp1)
            seq.addAction(Actions.moveBy(temp1.x, temp1.y, 0.1f))
            intensity -= decay
        }
        seq.addAction(Actions.moveBy(temp1.x, temp1.y, 0.1f))

        return seq
    }

    fun getShakes(count: Int, _intensity: Float, decay: Float = 1f): List<Action> {
        val shakes = mutableListOf<SequenceAction>()
        var intensity = _intensity
        tempOrigin.set(0f, 0f)
        for (i in 0 until count) {
            shakes.add(Actions.sequence())
        }
        while (intensity > 0) {
            temp1.set(0f, intensity)
            temp1.setAngle(tempOrigin.angle() + MathUtils.random(90, 270))
            tempOrigin.add(temp1)
            shakes.forEach {
                it.addAction(Actions.moveBy(temp1.x, temp1.y, 0.1f))
            }

            intensity -= decay
        }
        shakes.forEach {
            it.addAction(Actions.moveBy(-tempOrigin.x, -tempOrigin.y, 0.1f))
        }

        return shakes
    }

    fun fadeIn(duration: Float,interpolation: Interpolation = Interpolation.pow2Out): Action =
            Actions.sequence(Actions.alpha(0f),Actions.fadeIn(duration,interpolation))
}

fun spriteMoveTo(sprite: Sprite, endX: Float, endY: Float, align: Int = Align.bottomLeft, duration: Float = 0f, interpolation: Interpolation = Interpolation.linear): SpriteMoveToAction {
    val action = action<SpriteMoveToAction>(SpriteMoveToAction::class.java)
    action.endX = endX
    action.endY = endY
    action.sprite = sprite
    action.alignment = align
    action.duration = duration
    action.interpolation = interpolation
    return action
}

class SpriteMoveToAction : TemporalAction() {
    var endX: Float = 0f
    var endY: Float = 0f
    var startX: Float = 0f
    var startY: Float = 0f
    var sprite: Sprite? = null
    var alignment: Int = Align.bottomLeft
    override fun update(percent: Float) {
        sprite?.setPosition(startX + (endX - startX) * percent, startY + (endY - startY) * percent, alignment)
    }

    override fun begin() {
        startX = sprite?.getX(alignment)!!
        startY = sprite?.getY(alignment)!!
    }
}

fun delayedRun(delay: Float, runnable: () -> Unit): DelayAction {
    return Actions.delay(delay, Actions.run(runnable))
}

val acts = HAct()