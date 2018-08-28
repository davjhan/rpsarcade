package com.davjhan.rps

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.davjhan.hangdx.*
import com.davjhan.rps.data.Analytics
import com.davjhan.rps.data.GameSecret
import com.davjhan.rps.data.ObjType
import com.davjhan.rps.data.Secrets
import com.davjhan.rps.gamescreen.*
import ktx.actors.alpha

/**
 * Created by david on 2018-02-02.
 */
interface IGameScreen {
    val characters: MutableList<Character>
    val charsByType:  MutableList<List<Character>>
    val gameObjects: MutableList<GameObject>
    fun onCharacterHit(character: Character): Boolean
    val gameLayer: GameGroup
    val particles: Group
    val hud: Group
}

class GameScreen(val app: App) : BaseScreen(app.disp), IGameScreen {
    override val charsByType:  MutableList<List<Character>> = mutableListOf(
            listOf(),
            listOf(),
            listOf())

    val gameRunner = GameRunner(app, this)
    val data = GameData(this::onStateChanged,gameRunner::onScoreChanged)
    override val gameLayer = GameGroup()
    override val particles = Group()
    override val hud = Hud(app, this)
    var gameOverSinceClock=Clock(300f,true){
        if(Secrets.byId(ObjType.BOX, 2).unlock(app)){
            Secrets.onSecretUnlocked(app,this,Secrets.byId(ObjType.BOX, 2))
        }
    }
    var beforeTapFirstBoxClock=Clock(300f,true){
        if(Secrets.byId(ObjType.BOX, 4).unlock(app)){
            Secrets.onSecretUnlocked(app,this,Secrets.byId(ObjType.BOX, 4))
        }
    }
    val bg = TiledActor(app.art.bgTile[0], 11, 6)
    override val characters = mutableListOf<Character>()
    override val gameObjects = mutableListOf<GameObject>()


    override var paused: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            if (value) {
                val pauseOverlay = PauseOverlay(app,this) {
                    paused = false
                    pauseIgnorantActor = null
                }
                pauseIgnorantActor = pauseOverlay
                showOverlay(pauseOverlay)
            }
        }

    init {
        stage.spawn(bg, app.disp.hScreenWidth, app.disp.hScreenHeight, Align.center)
        stage.spawn(gameLayer)
        stage.spawn(particles)
        particles.touchable = Touchable.disabled
        stage.spawn(hud)
        gameRunner.start()

    }

    fun spawnChar(type: Int) {
        val char = Character(app, type, this)
        gameLayer.spawn(char, randRange(0f, app.disp.screenWidth - char.width),
                randRange(0f, app.disp.screenHeight - char.height))
        characters.add(char)
        gameObjects.add(char)
    }

    private fun onStateChanged(oldState: GameState, newState: GameState) {
        if (newState == GameState.gameEnd) {
            app.save.changeValues {
                it.gamesPlayed ++
                it.gamesSinceLastAd ++
            }
            Gdx.app.postRunnable {
                val victim = characters.first { it.state == CharState.lastManDown }
                victim.remove()

                val dim = SpriteActor(pixUtils.solidRect(colr.black, app.disp.screenWidth.toInt(),
                        app.disp.screenHeight.toInt()))
                dim.addAction(Actions.sequence(
                        Actions.alpha(0f),
                        Actions.alpha(0.7f, 0.1f, Interpolation.pow2Out)
                ))
                particles.addActor(victim)
                particles.addActorBefore(victim, dim)

            }
            characters.forEach { it.state = CharState.halt }
            app.sounds.play(app,app.sounds.lastManDown)
            app.sounds.stop(app,app.sounds.stretch)
            shakeScreen(5f)
            stage.addAction(delayedRun(2f) {
                val overlay = GameOverOverlay(app, this,data)
                stage.spawn(overlay)
                hud.remove()
                checkForSecrets()
            })
            app.bridge.reportAnalytic(Analytics.Category.gameplay,
                    Analytics.Action.gameOver,
                    value = data.score)
            if(data.score > app.save.highscore){
                app.bridge.reportAnalytic(Analytics.Category.gameplay,
                        Analytics.Action.highScore,
                        value = data.score)
            }
            gameLayer.paused = true


        }
    }
    fun checkForSecrets(){
        for(secret in Secrets.secrets.filter { it is GameSecret }){
            if(secret.canBeUnlocked(app.save,this)){
                secret.unlock(app)
                Secrets.onSecretUnlocked(app,this,secret)
                stage.addAction(delayedRun(1.5f){
                    checkForSecrets()
                })
                return
            }
        }
    }
    override fun hide() {
        super.hide()
        gameOverSinceClock.reset()
    }

    override fun update(delta: Float) {
        super.update(delta)
        if(data.state == GameState.start){
            beforeTapFirstBoxClock.update(delta)
        }
        if (paused) return
        for(i in 0..2){
            charsByType[i] = characters.filter { it.type == i }
        }
        if (data.state != GameState.gameEnd) {
            gameLayer.children.sort { a, b -> (b.y - a.y).toInt() }
        }else{
            gameOverSinceClock.update(delta)
        }


        gameRunner.update(delta)
        handleCollision(gameObjects)


    }


    override fun onBackPressed() {

    }

    override fun onCharacterHit(character: Character): Boolean {
        for (i in 0..2) {
            if (characters.filter { it != character }.count { it.type == i } == 0) {
                character.state = CharState.lastManDown
                data.state = GameState.gameEnd
                return false
            }
        }
        return true
    }

    fun shakeScreen(intensity: Float) {
        val shakes = acts.getShakes(2, intensity)
        bg.addAction(shakes[0])
        gameLayer.addAction(shakes[1])
//        hud.addAction(shakes[2])
    }

}

class GameRunner(val app: App, val game: GameScreen) {
    val spawnClock = Clock(5f) {
        val giftBox = createGiftBox()
        val x: Float
        val y: Float
        if (game.data.score % 2 == 0) {
            val targ = game.characters[randRange(0,game.characters.size)]
            x = targ.getX(Align.center)
            y = targ.getY(Align.center)
        } else {
            x = randRange(giftBox.width, app.disp.screenWidth - giftBox.width)
            y = randRange(giftBox.height, app.disp.screenHeight - giftBox.height)
        }
        giftBox.alpha = 0f
        giftBox.addAction(Actions.parallel(
                Actions.fadeIn(0.2f),
                acts.lookAtMe()
        ))
        game.gameLayer.spawn(giftBox, x, y, Align.center)
        game.gameObjects.add(giftBox)
    }

    fun update(delta: Float) {
        if (game.data.state == GameState.normal) {
            spawnClock.update(delta)
        }
    }

    fun start() {
        val tapButton = createTapButon(app)
        val kingGiftBox = KingGiftBox(app, game.particles) { x, y ->
            app.bridge.reportAnalytic(Analytics.Category.gameplay,
                    Analytics.Action.newGame)
            app.sounds.play(app,app.sounds.snap)
            val vec = Vector2(150f, 0f)

            for (i in 0..2) {
                vec.setAngle(90 + (360f / 3) * i)
                val king = createKing(i)
                game.gameLayer.spawn(king, x, y)
                game.shakeScreen(5f)
                king.addAction(Actions.moveTo(x + vec.x, y + vec.y, 0.3f, Interpolation.pow2))

                val dragSticker = Sticker(createTapButon(app, "Drag"), king, offsetY = 16f)
                dragSticker.addAction(Actions.delay(3f, Actions.run {
                    dragSticker.remove()
                }))
                game.particles.spawn(dragSticker)

                val giftbox = createGiftBox()
                val giftBoxVec = vec.cpy().setLength(100f).setAngle(-90 + (360f / 3) * i)
                game.gameLayer.spawn(giftbox, x, y)
                giftbox.addAction(Actions.moveTo(x + giftBoxVec.x, y + giftBoxVec.y, 0.3f, Interpolation.pow2))
            }
            game.data.score = 3
            game.hud.refresh()
            game.data.state = GameState.normal
            tapButton.remove()

        }

        game.gameLayer.spawn(kingGiftBox, app.disp.hScreenWidth, app.disp.hScreenHeight, Align.center)

        game.particles.spawn(tapButton, kingGiftBox.getX(Align.bottom),
                kingGiftBox.getY(Align.bottom) + 16f, Align.top)

    }

    private fun createChar(type: Int): Character {
        val char = Character(app, type, game)
        game.characters.add(char)
        game.gameObjects.add(char)
        return char
    }

    private fun createGiftBox(): Giftbox = Giftbox(app, game.particles) { x, y ->
        val char = createChar(randRange(0, 3))
        game.gameLayer.spawn(char, x, y)
        game.shakeScreen(2f)
        game.data.score++
        game.hud.refresh()
        char
    }

    private fun createKing(type: Int): King {
        val king = King(app, type, game)
        game.characters.add(king)
        game.gameObjects.add(king)
        return king
    }

    fun onScoreChanged(data:GameData){
        if(data.score <= 10){
            spawnClock.resetTime = 4f
        }else if(data.score <= 15){
            spawnClock.resetTime = 4.5f
        }
        else if(data.score <= 20){
            spawnClock.resetTime = 5f
        } else if(data.score <= 25){
            spawnClock.resetTime = 6f
        }
        else if(data.score <= 30){
            spawnClock.resetTime = 7f
        }
        else if(data.score <= 35){
            spawnClock.resetTime = 8f
        }else if(data.score <= 40){
            spawnClock.resetTime = 9f
        }else if(data.score <= 50){
            spawnClock.resetTime = 10f
        }
    }

}

enum class GameState {
    start,
    normal,
    gameEnd,
}

class GameData(val onChangeState: (oldState: GameState, newState: GameState) -> Unit,
               val onScoreChanged:(data:GameData)->Unit) {
    var score: Int = 0
    set(value) {
        field = value
        onScoreChanged(this)
    }

    var state = GameState.start
        set(value) {
            if (field == value) return
            onChangeState(field, value)
            field = value
        }
}


