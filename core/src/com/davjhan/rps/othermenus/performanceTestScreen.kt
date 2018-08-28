package com.davjhan.rps.othermenus

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.utils.Align
import com.davjhan.hangdx.BaseScreen
import com.davjhan.hangdx.GameGroup
import com.davjhan.hangdx.spawn
import com.davjhan.rps.App
import com.davjhan.rps.IGameScreen
import com.davjhan.rps.gamescreen.Character
import com.davjhan.rps.gamescreen.GameObject
import com.davjhan.rps.gamescreen.TiledActor
import com.davjhan.rps.gamescreen.handleCollision
import com.davjhan.rps.hlabel
import com.davjhan.rps.randRange

class PerformanceTestScreen(val app: App) : BaseScreen(app.disp), IGameScreen {

    var currStep = 0
    var gameOver = false
    override val characters: MutableList<Character> = mutableListOf()
    val bg = TiledActor(app.art.bgTile[0], 10, 5)
    val fpsCounter = hlabel(app, "FPS")
    override val hud: Group = Group()
    override val gameLayer = GameGroup()
    override val gameObjects: MutableList<GameObject> = mutableListOf()
    override val particles: Group = GameGroup()
    override val charsByType: MutableList<List<Character>> = mutableListOf(
            listOf(),
            listOf(),
            listOf())

    init {
        particles.touchable = Touchable.disabled

        stage.spawn(bg,disp.position(Align.center),Align.center)
        stage.spawn(gameLayer)
        stage.spawn(particles)
        stage.spawn(hud)
        stage.spawn(fpsCounter)

       for( i in 1..200){
           val char = Character(app, i%3, this)
           characters.add(char)
           gameObjects.add(char)
           gameLayer.spawn(char, randRange(0f,disp.screenWidth),randRange(0f,disp.screenHeight))
       }

    }

    override fun update(delta: Float) {
        super.update(delta)
        gameLayer.children.sort { a, b -> (b.y - a.y).toInt() }
        for(i in 0..2){
            charsByType[i] = characters.filter { it.type == i }
        }
        handleCollision(gameObjects)
        fpsCounter.setText("${Gdx.app.graphics.framesPerSecond}")
    }

    override fun onCharacterHit(character: Character): Boolean {

        return true
    }

    override fun onBackPressed() {
        app.controller.changeScreenTo(PerformanceTestScreen(app))
    }


}