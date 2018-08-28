package com.davjhan.hangdx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxScreen
import ktx.assets.disposeSafely

/**
 * Created by david on 2017-12-23.
 */
abstract class BaseScreen(val disp: Disp) : KtxScreen {
    val stage = Stage(FitViewport(disp.screenWidth, disp.screenHeight))
    open var paused:Boolean = false
    val inputMultiplexer = InputMultiplexer(stage)
    var pauseIgnorantActor: Actor? = null
    private val overlays = mutableListOf<BaseOverlay>()
    init {



        stage.addListener(object : InputListener() {
            override fun keyUp(event: InputEvent?, keycode: Int): Boolean {
                if (keycode.equals(Input.Keys.D)) {
                    stage.isDebugAll = !stage.isDebugAll
                }
                if (keycode.equals(Input.Keys.ESCAPE)) {
                    processBackButton()
                }
                if (keycode.equals(Input.Keys.BACK)) {
                    processBackButton()
                }
                return super.keyUp(event, keycode)
            }
        })

        stage.batch.shader = WhiteShader.whiteShader


        stage.batch.begin()
        stage.batch.shader.setUniformf("resolution",Gdx.graphics.width.toFloat(),Gdx.graphics.height.toFloat())
        stage.batch.end()
    }
    override fun show() {
        super.show()
        Gdx.input.inputProcessor = inputMultiplexer
    }

    fun processBackButton(){
        overlays.retainAll { it.getStage() == stage }
        if(overlays.size == 0)onBackPressed()
        else{
            overlays.last().onBackPressed()
        }
    }
    fun showOverlay(overlay:BaseOverlay,spawn:Boolean = true){
        overlays.add(overlay)
        if(spawn)stage.spawn(overlay.actor)
    }
    abstract fun onBackPressed()

    override fun render(delta: Float) {
        super.render(delta)
        update(delta)
        if(!paused)  stage.act()
        else pauseIgnorantActor?.act(delta)
        stage.draw()
    }

    open fun update(delta: Float) {

    }

    override fun dispose() {
        stage.disposeSafely()
        super.dispose()
    }


}

interface BaseOverlay{
    fun getStage():Stage
    val actor:Actor
    fun onBackPressed(){

    }
}