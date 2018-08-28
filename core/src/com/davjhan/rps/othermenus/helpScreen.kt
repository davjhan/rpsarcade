package com.davjhan.rps.othermenus

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.davjhan.hangdx.*
import com.davjhan.rps.*
import com.davjhan.rps.gamescreen.CharState
import com.davjhan.rps.gamescreen.Character
import com.davjhan.rps.gamescreen.GameObject
import com.davjhan.rps.gamescreen.TiledActor
import ktx.actors.onClick

class HelpScreen(val app: App) : BaseScreen(app.disp) {
    val background = SpriteActor(pixUtils.solidRect(colr.green,
            disp.screenWidth.toInt(),
            disp.screenHeight.toInt()))
    val grass: SpriteActor = TiledActor(app.art.bgTile[0], 3, 3)

    val body = htable{
        val tapNext = hbutton(app, false) {
            add(hiconAndLabel(SpriteActor(0.5f, app.art.handArrow.slice(0..1)), hlabel(app, "Start Game"), reverseOrder = true))
            onClick {
                app.controller.changeScreenTo(GameScreen(app))
            }
        }
        tapNext.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.delay(1f),
                Actions.alpha(1f),
                acts.lookAtMe()
        ))
        pWidth = app.disp.screenWidth
        pad(Size.xl)

        addThenRow(hlabel(app, "Units convert to the team of their captor.", app.font.reg1) {
            setWrap(true)
        }).grow().space(Size.xreg)
        addThenRow(hlabel(app, "The game ends if any one team goes extinct.", app.font.reg1) {
            setWrap(true)
        }).grow().space(Size.xl)
        addThenRow(tapNext).grow()
        pack()
    }


    init {

        stage.spawn(background)
        stage.spawn(grass, disp.position(Align.top).sub(0f,56f), Align.top)
        stage.spawn(hlabel(app, "How To Play"), disp.hScreenWidth, app.disp.screenHeight - Size.xreg, Align.top)
        stage.spawn(body, app.disp.hScreenWidth, 0f, Align.bottom)


        app.save.changeValues {
            it.gameFirstTime = false
        }
        createDiorama()

    }

    private fun createDiorama() {
        val dummyGame = object : IGameScreen {
            override val charsByType: MutableList<List<Character>> = mutableListOf()
            override val characters: MutableList<Character> = mutableListOf()
            override val gameObjects: MutableList<GameObject> = mutableListOf()
            override fun onCharacterHit(character: Character): Boolean = true

            override val gameLayer: GameGroup = GameGroup()
            override val particles: Group = Group()
            override val hud: Group = Group()
        }

        stage.spawn(dummyGame.gameLayer)
        stage.spawn(dummyGame.particles)
        val vec = Vector2(80f, 0f)
        val center = grass.getPos(Align.center).sub(0f, 20f)
        val chars = mutableListOf<Character>()
        for (i in 0..2) {
            val char = Character(app, i, dummyGame)
            vec.setAngle(90 + 120f * i)
            dummyGame.gameLayer.spawn(char, center.cpy().add(vec), Align.center)
            char.displayMode = true
            chars.add(char)
        }
        val protagonist = Character(app, 0, dummyGame)
        protagonist.displayMode = true
        protagonist.displayModeAction = Actions.forever(Actions.sequence(
                Actions.moveToAligned(chars[1].getPos(Align.topRight).x, chars[1].getPos(Align.topRight).y,
                        Align.center, 0.9f),
                Actions.run { protagonist.changeType(1) },
                Actions.moveToAligned(chars[2].getPos(Align.topLeft).x, chars[2].getPos(Align.topLeft).y,
                        Align.center, 0.9f),
                Actions.run { protagonist.changeType(2) },
                Actions.moveToAligned(chars[0].getPos(Align.bottom).x, chars[0].getPos(Align.bottom).y,
                        Align.center, 0.9f),
                Actions.run { protagonist.changeType(0) }
        ))
        stage.spawn(protagonist, center, Align.center)
        protagonist.state = CharState.normal
        protagonist.addAction(protagonist.displayModeAction)
    }

    override fun onBackPressed() {

    }

}