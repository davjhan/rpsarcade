package com.davjhan.rps.othermenus

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.davjhan.hangdx.*
import com.davjhan.rps.*
import com.davjhan.rps.data.Analytics
import com.davjhan.rps.data.ObjType
import com.davjhan.rps.gamescreen.*
import ktx.actors.onClick
import ktx.assets.dispose

class TutorialScreen(val app: App) : BaseScreen(app.disp), IGameScreen {
    override val charsByType: MutableList<List<Character>> = mutableListOf(
            listOf(),
            listOf(),
            listOf())
    var currStep = 0
    var gameOver = false
    override val characters: MutableList<Character> = mutableListOf()
    val bg = TiledActor(app.art.bgTile[0], 11, 6)
    val scoreCounter = hiconAndLabel(app.art.tinyIcons[0], hlabel(app, "Score")) {
        pad(Size.sm)
    }
    val steps = listOf(
            {
                howToPlay.addAction(Actions.sequence(
                        Actions.moveToAligned(Size.reg, -16f, Align.topLeft),
                        Actions.delay(0.2f),
                        Actions.moveToAligned(Size.reg, Size.reg, Align.bottomLeft, 0.6f, Interpolation.circleOut),
                        Actions.run {
                            val char = Character(app, ObjType.ROCK, this)
                            addChar(char, disp.position(Align.center))
                            val dragSticker = Sticker(createTapButon(app, "Drag"), char, offsetY = 16f)
                            char.onClick {
                                dragSticker.remove()
                                hideNext(false)
                            }
                            particles.spawn(dragSticker)

                        }
                ))
                hideNext(true)
            },
            {
                clearChars()
                hideNext(true)
                setHelpText("This is a king rock.")
                howToPlay.pack()
                stage.addAction(delayedRun(0.5f) {
                    val king = King(app, ObjType.ROCK, this)
                    addChar(king, disp.position(Align.center))
                    val dragSticker = Sticker(createTapButon(app, "Drag"), king, offsetY = 16f)
                    stage.addAction(delayedRun(0.5f) {

                        val center = disp.position(Align.center).add(-10f, 90f)
                        for (i in 0..4) {
                            val char = Character(app, ObjType.ROCK, this)
                            stage.addAction(delayedRun(i * 0.1f) {
                                addChar(char, center.cpy().add(50f * (i - 2), 0f))
                            })
                        }
                        stage.addAction(delayedRun(0.7f) {
                            king.onClick {
                                dragSticker.remove()
                                hideNext(false)
                            }

                            particles.spawn(dragSticker)

                            setHelpText("Holding a king attracts all units of its type.")
                            howToPlay.pack()
                        })

                    })

                })

            },
            {
                clearChars()
                setHelpText("There are three types of units in the game.")


                howToPlay.addAction(Actions.sequence(
                        Actions.moveToAligned(Size.reg, -16f, Align.topLeft),
                        Actions.delay(0.2f),
                        Actions.moveToAligned(Size.reg, Size.reg, Align.bottomLeft, 0.6f, Interpolation.circleOut),
                        Actions.run {
                            stage.addAction(delayedRun(0.3f) {
                                val center = disp.position(Align.center).add(-20f, 40f)
                                for (i in 0..2) {
                                    val char = Character(app, i, this)
                                    char.displayMode = true
                                    stage.addAction(delayedRun(i * 0.2f) {
                                        gameLayer.spawn(char, center.cpy().add(60f * (i - 1), 0f))
                                    })

                                }
                            })
                        }
                ))

            },
            {
                clearChars()
                setHelpText("Rocks like to chase after scissors.")
                val lastLine = wrappedLabel(app,
                        hlabel(app, ">Drag the rock onto the scissor.", app.font.green1) { setWrap(true) })
                lastLine.name = "last"
                howToPlay.removeActor(nextButton)
                howToPlay.row()
                howToPlay.add(lastLine).grow()
                lastLine.addAction(Actions.forever(acts.lookAtMe(0.3f, 1.03f)))
                howToPlay.pack()
                howToPlay.addAction(Actions.sequence(
                        Actions.moveToAligned(Size.reg, -16f, Align.topLeft),
                        Actions.delay(0.2f),
                        Actions.moveToAligned(Size.reg, Size.reg, Align.bottomLeft, 0.6f, Interpolation.circleOut),
                        Actions.run {

                            val rock = Character(app, ObjType.ROCK, this)
                            val scis = Character(app, ObjType.SCISSORS, this)
                            addChar(rock, disp.position(Align.center).add(50f, 0f))
                            addChar(scis, disp.position(Align.center).add(-50f, 0f))

                            val hitSticker = Sticker(createVertIconAndText(app, app.art.largeIcons[8], "Hit"), scis, offsetY = 16f)
                            val dragSticker = Sticker(createTapButon(app, "Drag"), rock, offsetY = 16f)
                            hitSticker.addAction(Actions.delay(3f, Actions.run {
                                hitSticker.remove()
                            }))
                            dragSticker.addAction(Actions.delay(3f, Actions.run {
                                dragSticker.remove()
                            }))
                            particles.spawn(hitSticker)
                            particles.spawn(dragSticker)
                        }
                ))


            },
            {
                stage.addAction(delayedRun(0.9f) {
                    setHelpText("Units convert to the type of their captor.")

                    howToPlay.removeActor(howToPlay.findActor<Actor>("last"))
                    howToPlay.row()
                    howToPlay.add(nextButton).right()
                    howToPlay.pack()

                    app.sounds.play(app,app.sounds.buttonDownPop)

                })
            },
            {
                clearChars()

                setHelpText("Paper -> Rock -> Scissors, as you'd expect.")
                howToPlay.removeActor(nextButton)
                howToPlay.pack()
                howToPlay.addAction(Actions.sequence(
                        Actions.moveToAligned(Size.reg, -16f, Align.topLeft),
                        Actions.delay(0.2f),
                        Actions.moveToAligned(Size.reg, Size.reg, Align.bottomLeft, 0.6f, Interpolation.circleOut),
                        Actions.run {

                            val vec = Vector2(90f, 0f)
                            val center = disp.position(Align.center).add(0f, 40f)
                            for (i in 0..2) {
                                val char = Character(app, i, this)
                                vec.setAngle(90 + 120f * i)
                                val spawnPos = center.cpy().add(vec)
                                stage.addAction(delayedRun(i * 0.2f) {
                                    addChar(char, spawnPos)
                                })
                            }
                        }
                ))
            },
            {
                setHelpText("You must keep at least one of each unit type alive.")


                howToPlay.row()
                val lastLine = wrappedLabel(app, hlabel(app, "If a unit type becomes extinct, it's game over!", app.font.red1) { setWrap(true) })
                val startButton = hbutton(app) {
                    setBackgrounds(app.art.bg.yellowButton)
                    add(hiconAndLabel(app.art.tinyIcons[9],
                            hlabel(app, "Start Game!", app.font.yellow1),
                            Size.sm, reverseOrder = true))
                    onClick {
                        app.save.changeValues {
                            it.gameFirstTime = false
                        }
                        app.bridge.reportAnalytic(Analytics.Category.gameplay,
                                Analytics.Action.tutorialPassed)
                        app.controller.changeScreenTo(GameScreen(app))
                        this@TutorialScreen.dispose()
                    }
                    addAction(acts.lookAtMeShine(this))
                }
                howToPlay.addThenRow(lastLine).grow()
                howToPlay.addThenRow(startButton).grow()
                howToPlay.pack()

                howToPlay.addAction(Actions.sequence(
                        Actions.moveToAligned(Size.reg, -16f, Align.topLeft),
                        Actions.delay(2f),
                        Actions.moveToAligned(Size.reg, Size.reg, Align.bottomLeft, 0.6f, Interpolation.circleOut)
                ))

                lastLine.addAction(Actions.sequence(
                        Actions.alpha(0f),
                        Actions.delay(3.5f),
                        Actions.fadeIn(0.4f)
                ))
                startButton.addAction(Actions.sequence(
                        Actions.alpha(0f),
                        Actions.delay(5f),
                        Actions.fadeIn(0.4f),
                        acts.lookAtMeShine(startButton),
                        Actions.forever(acts.lookAtMe(0.3f, 1.02f))
                ))

            }

    )
    val nextButton: HButton = hbutton(app, naked = true, isSquishy = true) {
        add(hiconAndLabel(app.art.tinyIcons[9], hlabel(app, "Next"), reverseOrder = true))
        pad(Size.sm)
        onClick(this@TutorialScreen::nextSlide)
    }
    override val gameLayer = GameGroup()
    override val gameObjects: MutableList<GameObject> = mutableListOf()
    override val particles: Group = GameGroup()

    val instructions = wrappedLabel(app, hlabel(app, "Drag a unit to move it around.") { setWrap(true) })
    val tutLabel = hlabel(app, "Tutorial (1/${steps.size})", app.font.sub1)
    val howToPlay: HTable = htable {
        background = app.art.bigNines.paper
        pad(Size.reg)
        padTop(Size.xl)
        padBottom(Size.lg)
        pWidth = disp.screenWidth - (Size.reg * 2)
        addThenRow(instructions).grow()
        add(nextButton).right()
        pack()
        addDecal(hiconAndLabel(app.art.tinyIcons[8], tutLabel), Align.top, Align.center, yOffset = -4f)
    }

    private fun hideNext(hide: Boolean = true) {
        if (hide) {
            nextButton.isVisible = false
        } else {
            nextButton.isVisible = true
            nextButton.addAction(acts.lookAtMeShine(nextButton))

            app.sounds.play(app,app.sounds.buttonDownPop)
        }
    }

    override val hud: Group = hgroup {
        spawn(scoreCounter, disp.position(Align.topRight, Size.reg), Align.topRight)
        spawn(howToPlay, disp.position(Align.bottom, Size.reg), Align.bottom)

    }

    private fun clearChars() {
        gameLayer.clear()
        gameObjects.clear()
        characters.clear()
        particles.clear()
        stage.root.clearActions()
    }

    init {
        particles.touchable = Touchable.disabled
        nextButton.addAction(acts.lookAtMeShine(nextButton))
        stage.spawn(bg, disp.position(Align.center), Align.center)
        stage.spawn(gameLayer)
        stage.spawn(particles)
        stage.spawn(hud)

        steps[currStep]()

    }

    private fun addChar(char: Character, pos: Vector2) {
        characters.add(char)
        gameObjects.add(char)
        gameLayer.spawn(char, pos, Align.center)
    }

    override fun update(delta: Float) {
        super.update(delta)
        for (i in 0..2) {
            charsByType[i] = characters.filter { it.type == i }
        }
        gameLayer.children.sort { a, b -> (b.y - a.y).toInt() }
        handleCollision(gameObjects)
    }

    fun setHelpText(text: String) {
        instructions.setText(text)
        instructions.addAction(acts.lookAtMe(scaleUp = 1.05f))
        howToPlay.pack()
    }

    fun nextSlide() {
        currStep++
        this@TutorialScreen.steps[currStep]()
        tutLabel.setText("Tutorial (${currStep + 1}/${steps.size})")
    }

    override fun onCharacterHit(character: Character): Boolean {
        if (currStep == 3) {
            Gdx.app.postRunnable {
                nextSlide()
            }
        } else if (currStep == 5) {
            if (gameOver) return true
            gameOver = true

            Gdx.app.postRunnable {

                app.sounds.play(app,app.sounds.lastManDown)
                character.remove()
                characters.clear()
                val dim = SpriteActor(pixUtils.solidRect(colr.black, app.disp.screenWidth.toInt(),
                        app.disp.screenHeight.toInt()))
                dim.addAction(Actions.sequence(
                        Actions.alpha(0f),
                        Actions.alpha(0.7f, 0.1f, Interpolation.pow2Out)
                ))
                particles.addActor(character)
                particles.addActorBefore(character, dim)
//
//                val gameOver = SpriteActor(app.art.gameover[0])
//                gameOver.moveBy(0f, 60f)
//                gameOver.addAction(Actions.sequence(
//                        Actions.alpha(0f),
//                        Actions.parallel(
//                                Actions.fadeIn(0.1f, Interpolation.pow2Out),
//                                Actions.moveBy(0f, 8f, 0.1f, Interpolation.pow2Out)
//                        ),
//                        Actions.moveBy(0f, -60f - 8f, 0.2f, Interpolation.pow3In),
//                        Actions.run {
//                            throwParticles(particles, ParticleType.poof, 20,
//                                    Vector2(gameOver.getX(Align.bottom), gameOver.getY(Align.bottom)),
//                                    70f, 16f)
//                        },
//                        Actions.scaleTo(1.05f, 0.95f, 0.05f, Interpolation.pow2Out),
//                        Actions.scaleTo(1f, 1f, 0.05f, Interpolation.pow2Out)
//                ))
//                particles.spawn(gameOver,disp.position(Align.center).add(0f,90f),Align.bottom)
                nextSlide()
            }


            return false
        }
        return true
    }

    override fun dispose() {
        super.dispose()
        characters.dispose()
    }

    override fun onBackPressed() {

    }


}