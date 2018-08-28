package com.davjhan.rps.gamescreen

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.davjhan.hangdx.*
import com.davjhan.rps.*
import com.davjhan.rps.othermenus.MainScreen
import com.davjhan.rps.othermenus.UiCommon
import com.davjhan.rps.othermenus.UiCommon.makePlayButton
import com.davjhan.rps.othermenus.UiCommon.makeRemoveAdsButton
import com.davjhan.rps.othermenus.UiCommon.makeSecretsButton
import ktx.actors.onClick

class DummyGameOverScreen(val app: App) : BaseScreen(app.disp) {
    init {
        val gameData = GameData({ x, y -> }, {})
        gameData.score = app.save.highscore+1
        val overlay = GameOverOverlay(app, this, gameData)
        stage.spawn(overlay)
    }


    override fun onBackPressed() {
        app.controller.changeScreenTo(DummyGameOverScreen(app))
    }

}

class GameOverOverlay(val app: App, val currScreen: BaseScreen, val gameData: GameData) : HTable({}) {
    var isHighscore = gameData.score > app.save.highscore
    //    var gameOver = htable() {
//        add(hlabel(app, "Game Over", app.font.reg2) {
//            pad(Size.sm)
//            setWrap(true)
//            setAlignment(Align.center)
//        })
//    }
    val backButton = hiconbutton(app,app.art.largeIcons[0]) {
        onClick {
            app.controller.changeScreenTo(MainScreen(app))
        }
    }
    val gameOver = SpriteActor(app.art.gameover[if(isHighscore)1 else 0])
    val settingsButton = UiCommon.makeSettingsButton(app,currScreen)
    val score = hiconAndLabel(app.art.tinyIcons[0], hlabel(app, "Score: ${gameData.score}"))
    val highscore = hiconAndLabel(app.art.tinyIcons[1], hlabel(app, "Highscore: ${getHighScore()}",app.font.yellow1))
    val particles: Group = Group()
    val bgParticles: Group = Group()
    val bg = SpriteActor(pixUtils.solidRect(colr.shadow, app.disp.screenWidth.toInt(), app.disp.screenHeight.toInt() + 4))
    //    val bg = PanningTiledActor(app.art.bgTile[2],10,5,4f)
    val particleClock = Clock(0.8f) {
        throwParticles(app,particles, ParticleType.sparkle, 2,
                Vector2(gameOver.getX(Align.center), gameOver.getY(Align.center)),
                70f, 16f)

    }
    val confettiClock = Clock(0.4f) {
        throwParticles(app,bgParticles, ParticleType.bigConfetti, 3,
                Vector2(app.disp.position(Align.top,-16f)),
                app.disp.hScreenWidth, 0f)

    }

    fun getHighScore(): Int {
        if (gameData.score > app.save.highscore) {
            app.save.changeValues {
                it.highscore = gameData.score
            }
            return gameData.score
        }
        return app.save.highscore
    }

    init {
        gameOver.setOrigin(Align.bottom)
        fillScreen(app.disp)
        addActor(bg)
        addActor(bgParticles)
        bg.addAction(acts.fadeIn(0.5f))
        particles.touchable = Touchable.disabled

        center()
        addThenRow(gameOver).padTop(Size.xl).spaceBottom(Size.reg).growX().center()
        addThenRow(score).spaceBottom(0f)
        addThenRow(highscore).spaceBottom(Size.xl)
        add(htable {
            addThenRow(makePlayButton(app,currScreen)).fillX()
            addThenRow(makeSecretsButton(app) {app.controller.changeScreenTo(currScreen)}).fillX().spaceBottom(Size.sm)
            if(!app.save.removeAds) addThenRow(makeRemoveAdsButton(app,currScreen)).fillX()
            top()
        }).width(120f)

        addActor(particles)
        spawn(settingsButton, app.disp.position(Align.topRight, Size.reg), Align.topRight)
        spawn(backButton, app.disp.position(Align.topLeft, Size.reg), Align.topLeft)
        pack()
        initActions()
    }

    private fun initActions() {
        val height = 60f
        particleClock.enabled = false
        gameOver.moveBy(0f, height)
        gameOver.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.parallel(
                        Actions.fadeIn(0.1f, Interpolation.pow2Out),
                        Actions.moveBy(0f, 8f, 0.1f, Interpolation.pow2Out)
                ),
                Actions.moveBy(0f, -height - 8f, 0.2f, Interpolation.pow3In),
                Actions.run {
                    if(isHighscore) app.sounds.play(app,app.sounds.highscore)
                    else app.sounds.play(app,app.sounds.gameover)
                    throwParticles(app,particles, ParticleType.poof, 20,
                            Vector2(gameOver.getX(Align.bottom), gameOver.getY(Align.bottom)),
                            70f, 16f)
                },
                Actions.scaleTo(1.05f, 0.95f, 0.05f, Interpolation.pow2Out),
                Actions.scaleTo(1f, 1f, 0.05f, Interpolation.pow2Out),
                Actions.run {
                    particleClock.enabled = true
                }
        ))
        if(isHighscore){
            app.sounds.play(app,app.sounds.fanfare)
        }
    }

    override fun act(delta: Float) {
        super.act(delta)

        particleClock.update(delta)
        if(isHighscore){
            confettiClock.update(delta)
        }

    }

    override fun setParent(parent: Group?) {
        super.setParent(parent)
        if (parent != null) onShow()
    }

    fun onShow() {
        addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.fadeIn(0.3f)
        ))

    }

}
