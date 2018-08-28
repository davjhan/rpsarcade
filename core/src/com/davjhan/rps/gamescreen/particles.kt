package com.davjhan.rps.gamescreen

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils.random
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Pool
import com.davjhan.hangdx.SpriteActor
import com.davjhan.hangdx.reset
import com.davjhan.hangdx.spawn
import com.davjhan.rps.App
import com.davjhan.rps.randRange
import ktx.actors.alpha
import ktx.collections.toGdxArray
import net.dermetfan.gdx.graphics.g2d.AnimatedSprite

class Particle(val app: App, val type: ParticleType) : SpriteActor(), Pool.Poolable {
    var playMode: Animation.PlayMode = Animation.PlayMode.NORMAL
    val sprite: AnimatedSprite
    val makeAction:()->Action
    override fun reset() {
        clearActions()
        sprite.reset()
        alpha = 1f
    }

    init {
        val frames: Array<TextureRegion>

        when (type) {
            ParticleType.smoke,
            ParticleType.poof,
            ParticleType.dust,
            ParticleType.sparkle -> {
                val artIndex = when (type) {
                    ParticleType.smoke -> 0
                    ParticleType.sparkle -> 1
                    ParticleType.poof -> 2
                    ParticleType.dust -> 3
                    else -> 0
                }
                frames = app.art.particles[artIndex]
                makeAction = {Actions.moveBy(randRange(-4f, 4f), randRange(8f, 16f), 0.4f)}
                playMode = Animation.PlayMode.NORMAL
            }
            ParticleType.confetti -> {
                frames = app.art.smallConfetti[randRange(0, 3)]
                makeAction = {Actions.sequence(
                        Actions.moveBy(randRange(-4f, 4f), randRange(24f, 32f), 0.2f, Interpolation.pow4Out),
                        Actions.parallel(
                                Actions.moveBy(randRange(-4f, 4f), -24f, 1.5f),
                                Actions.delay(randRange(1f, 1.5f), Actions.fadeOut(0.2f))
                        )
                )}
                playMode = Animation.PlayMode.LOOP

            }
            ParticleType.bigConfetti -> {
                frames = app.art.bigConfetti[randRange(0, 3)]
                makeAction = {Actions.sequence(
                        Actions.moveBy(randRange(-32f, 32f), -app.disp.screenHeight - frames[0].regionHeight,
                                randRange(3f, 5f))
                )}
                playMode = Animation.PlayMode.LOOP

            }
        }
        scaleX = if (random.nextFloat() > 0.5f) -1f else 1f
        scaleY = if (random.nextFloat() > 0.5f) -1f else 1f
        sprite = AnimatedSprite(Animation<TextureRegion>(0.2f, frames.toGdxArray(), playMode))

        setSprite(sprite)
    }

    private fun destroy() {
        remove()
        app.particlePools[type]!!.free(this)

    }

    override fun act(delta: Float) {
        super.act(delta)
        if (playMode == Animation.PlayMode.NORMAL && sprite.isAnimationFinished) {
            destroy()
        }
    }

    override fun addedToStage() {
        super.addedToStage()
        addAction(makeAction())
        if (playMode == Animation.PlayMode.LOOP) {
            addAction(Actions.after(Actions.run {
                destroy()
            }))
        }
    }
}

enum class ParticleType {
    sparkle, confetti, bigConfetti, poof, smoke, dust
}

val MAX_PARTICLES = 300

fun throwRadialParticles(app: App, group: Group, type: ParticleType, _amount: Int,
                         pos: Vector2, radius: Float, highPriority: Boolean = false) {
    val amount = if (group.children.size > MAX_PARTICLES * 0.75 && !highPriority) 1 else _amount
    val vec = Vector2(0f, radius)
    for (i in 0 until amount) {
        if (group.children.size > MAX_PARTICLES && !highPriority) return
        vec.setAngle(i * (360f / amount))
        val particle = app.particlePools.get(type)!!.obtain()
        group.spawn(particle, pos.x + vec.x,
                pos.y + vec.y, Align.center)

    }
}

fun throwParticles(app: App, group: Group, type: ParticleType, amount: Int,
                   pos: Vector2, range: Float, highPriority: Boolean = false) = throwParticles(app, group, type, amount, pos, range, range, highPriority)

fun throwParticles(app: App, group: Group, type: ParticleType, _amount: Int,
                   pos: Vector2, xRange: Float, yRange: Float, highPriority: Boolean = false) {

    val amount = if (group.children.size > MAX_PARTICLES * 0.75 && !highPriority) 1 else _amount
    for (i in 0 until amount) {

        if (group.children.size > MAX_PARTICLES && !highPriority) return
        val particle = app.particlePools.get(type)!!.obtain()
        group.spawn(particle, pos.x + randRange(-xRange, xRange),
                pos.y + randRange(-yRange, yRange), Align.center)

    }
}