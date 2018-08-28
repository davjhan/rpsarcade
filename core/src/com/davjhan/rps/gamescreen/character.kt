package com.davjhan.rps.gamescreen

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.davjhan.hangdx.Disp
import com.davjhan.hangdx.SpriteActor
import com.davjhan.hangdx.acts
import com.davjhan.hangdx.moveBy
import com.davjhan.rps.App
import com.davjhan.rps.Clock
import com.davjhan.rps.IGameScreen
import com.davjhan.rps.Vibrate
import com.davjhan.rps.data.ObjType

abstract class GameObject() : SpriteActor() {
    val center get() = getPos(Align.bottom)
    abstract val radius: Float
    open fun isCollidable(): Boolean = true
    open fun handleCollision(other: GameObject) {}
}

open class Character(val app: App, var type: Int, val gameScreen: IGameScreen) : GameObject() {
    val NORMAL_SPEED = 0.32f
    val KING_CALL_SPEED = 1.5f
    override val radius: Float = 20f
    var timeSinceSpawn: Float = 0f

    val velocity = Vector2()
    val fling: Vector2 = Vector2()
    var charSprite: Sprite? = null
    val exclamationMark: Sticker = Sticker(SpriteActor(0.2f, app.art.characterCrowns.slice(0..3)), this, Align.top, Align.bottom)
    open val textureSheet = app.art.characters
    var displayMode = false
    var displayModeAction: Action? = null
    var state = CharState.birth
        set(value) {
            if (field == value) return

            if (displayModeAction != null) {
                actions.removeAll { it != displayModeAction }
            } else {
                clearActions()
            }
            when (value) {
                CharState.selected -> {
                    sprites[0].y = 16f
                    sprites.add(0, Sprite(textureSheet[ObjType.OTHER][ObjType.Others.SHADOW]))
                    analytics.pickedUpCount++
                    Vibrate.short(app)
                    onPicked(true)
                }
                CharState.normal -> {
                    addAction(Actions.forever(acts.hop()))
                }
                CharState.halt -> {
                    if (field == CharState.lastManDown) return
                }
            }
            if (field == CharState.selected) {
                sprites.removeAt(0)
                sprites[0].y = 0f
                analytics.recentPickedUpTime = 0f
                onPicked(false)
            }
            field = value
        }
    val sparkleClock = Clock(0.4f) {
        if (app.save.graphicsHigh) throwParticles(app, gameScreen.particles, ParticleType.sparkle, 1, getPos(Align.center), width / 2)
    }
    val dustClock = Clock(0.05f) {
        if (app.save.graphicsHigh) throwParticles(app, gameScreen.gameLayer, ParticleType.dust, 1, getPos(Align.bottom), 8f)
    }

    init {
        setSize(24f, 30f)
    }

    protected open fun onPicked(up: Boolean) {

    }

    val touchListener = object : InputListener() {
        val lastPos: Vector2 = Vector2()
        val lastDelta: Vector2 = Vector2()
        override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
            if (state != CharState.normal || displayMode) return false

            gameScreen.characters.filter { it.state == CharState.selected }
                    .forEach { it.state = CharState.normal }

            state = CharState.selected
            lastPos.set(x, y)
            app.sounds.play(app, app.sounds.pickup[type])

            return true
        }

        override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
            super.touchUp(event, x, y, pointer, button)
            if (state != CharState.selected) return
            fling.set(lastDelta)
            app.sounds.play(app, app.sounds.dropoff)
            state = CharState.normal
        }

        override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
            super.touchDragged(event, x, y, pointer)
            if (state != CharState.selected) return
            lastDelta.set(x - lastPos.x, y - lastPos.y)

            moveBy(lastDelta)
        }
    }
    val analytics = CharAnalytics()

    override fun addedToStage() {
        if (state == CharState.birth) {
            addListener(touchListener)
            changeType(type)
        }
    }

    override fun isCollidable(): Boolean = state != CharState.selected


    override fun act(delta: Float) {
        val deltaPos = Vector2()
        timeSinceSpawn += delta

        if (state == CharState.birth) {
            if (timeSinceSpawn >= 0.3f) {
                state = CharState.normal
            }
        }
        if (state == CharState.normal) {
            val target: Character?
            val myKing = gameScreen.charsByType[type].filter {
                it.state == CharState.selected
                        && it is King
            }
            if (!myKing.isEmpty()) {
                target = myKing[0]
                velocity.set(0f, KING_CALL_SPEED)
                if (exclamationMark.stage == null) {
                    gameScreen.particles.addActor(exclamationMark)
                    exclamationMark.addAction(acts.lookAtMe())
                }
            } else {
                target = gameScreen.charsByType[(3 + type - 1) % 3].minBy { Vector2.dst2(x, y, it.x, it.y) }
                velocity.set(0f, NORMAL_SPEED)
                if (exclamationMark.stage != null) {
                    exclamationMark.remove()
                }
            }
            if (target != null) {
                val del = target.getPos(Align.bottom).sub(0f, 4f).sub(getPos(Align.bottom))
                val angle = (del).angle()

                velocity.setAngle(angle)
                if (del.len() > 5f) {
                    deltaPos.add(velocity)
                }

            }

            if (timeSinceSpawn < 2f) {
                sparkleClock.update(delta)
            }
            if (fling.len() > 0.5f) {
                dustClock.update(delta)
            }
        }
        if (state == CharState.selected) {
            analytics.recentPickedUpTime += delta
            analytics.longestPickedUpTime = maxOf(analytics.longestPickedUpTime, analytics.recentPickedUpTime)
        }
        fling.scl(0.8f)
        deltaPos.add(fling.cpy().clamp(0f, 7f))
        moveBy(deltaPos)
        super.act(delta)
    }

    override fun handleCollision(other: GameObject) {
        if (other is Character) {
            if (other.type == (3 + type + 1) % 3) {
                if (other.state == CharState.normal && state == CharState.normal) {
                    if (gameScreen.onCharacterHit(this)) {
                        changeType(other.type)
                        app.sounds.play(app, app.sounds.converted[other.type])
                        if (app.save.graphicsHigh) throwParticles(app, gameScreen.particles, ParticleType.poof, 3, getPos(Align.bottom), width / 2)
                    } else {
                        type = other.type
                        addAction(Actions.sequence(Actions.parallel(
                                acts.blinkWhite(3, this),
                                Actions.scaleTo(1.2f, 1.2f, 0.1f, Interpolation.elasticOut),
                                acts.sideShake(6f, 6)
                        ), Actions.delay(0.5f),
                                Actions.fadeOut(1.5f)
                        ))
                        if (app.save.graphicsHigh) throwRadialParticles(app, gameScreen.hud, ParticleType.poof, 16, getPos(Align.bottom), width, highPriority = true)

                    }
                }
            }
        }
    }

    override fun remove(): Boolean {
        gameScreen.characters.remove(this)
        gameScreen.gameObjects.remove(this)
        exclamationMark.remove()
        return super.remove()
    }

    open fun changeType(newType: Int) {
        if (type != newType) analytics.convertedCount++
        this.type = newType
        state = CharState.birth

        addAction(acts.lookAtMe())
        sprites.clear()
        if (charSprite == null) charSprite = Sprite(textureSheet[newType][app.save.selectedSkin[newType]])
        else charSprite!!.setRegion(textureSheet[newType][app.save.selectedSkin[newType]])
        setSprite(charSprite!!)
        if (gameScreen.characters.size < 20) {
            timeSinceSpawn = 0f
            if (app.save.graphicsHigh) throwParticles(app, gameScreen.particles, ParticleType.sparkle, 3, getPos(Align.center), 8f)
        }
        addAction(acts.flashWhite(this))

    }

    override fun moveBy(x: Float, y: Float) {
        super.moveBy(x, y)

        val hitBounds = handleBounds(app.disp, this)
        if (state != CharState.selected && hitBounds && !fling.isZero) {
            analytics.hitWallCount++
            fling.setZero()
            velocity.setZero()
            app.sounds.play(app, app.sounds.wallhit)
        }
    }


    class CharAnalytics(
            var pickedUpCount: Int = 0,
            var hitWallCount: Int = 0,
            var convertedCount: Int = 0,
            var recentPickedUpTime: Float = 0f,
            var longestPickedUpTime: Float = 0f
    )
}

enum class CharState {
    birth,
    normal,
    selected,
    halt,
    lastManDown,
}


class King(app: App, type: Int, gameScreen: IGameScreen) : Character(app, type, gameScreen) {
    override val radius: Float = 24f
    override val textureSheet = app.art.bigCharacters

    init {
        setSize(24f, 30f)
    }

    var pickedUp = false

    override fun act(delta: Float) {
        super.act(delta)

    }
}

fun handleBounds(disp: Disp, actor: Character): Boolean {
    var handledBounds = false
    if (actor.x < 0) {
        actor.x = 0f
        actor.velocity.x *= -1
        handledBounds = true
    }
    if (actor.right > disp.screenWidth) {
        actor.x = disp.screenWidth - actor.width
        actor.velocity.x *= -1
        handledBounds = true
    }
    if (actor.y < 0) {
        actor.y = 0f
        actor.velocity.y *= -1
        handledBounds = true
    }
    if (actor.top > disp.screenHeight) {
        actor.y = disp.screenHeight - actor.height
        actor.velocity.y *= -1
        handledBounds = true
    }
    return handledBounds
}