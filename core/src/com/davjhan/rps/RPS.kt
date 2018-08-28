package com.davjhan.rps

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Scaling
import com.davjhan.hangdx.Disp
import com.davjhan.hangdx.GameLoader
import com.davjhan.hangdx.HImage
import com.davjhan.hangdx.WhiteShader.Companion.frag
import com.davjhan.hangdx.WhiteShader.Companion.vert
import com.davjhan.hangdx.WhiteShader.Companion.whiteShader
import com.davjhan.rps.data.*
import com.davjhan.rps.othermenus.MainScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.inject.Context
import ktx.log.info

class RPS(val bridge:Bridge, val iap: IAP, val flavour: String = FLAVOUR_DEV) : KtxGame<KtxScreen>() {

    var batch: Batch? = null
    var logo:TextureRegion? = null
    var pic: HImage? = null
    var loaded:Boolean = false
    var disp:Disp? = null
    override fun create() {
        batch = SpriteBatch()
        logo = TextureRegion(Texture(Gdx.app.files.internal("art/Logo.png")))
        disp = Disp(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        pic = HImage(logo!!)
        pic!!.setScaling(Scaling.fillX)
        pic!!.setSize(127*disp!!.ppi,173.5f*disp!!.ppi)

        pic!!.setPosition((Gdx.graphics.width/2f)-(pic!!.width/2f), (Gdx.graphics.height/2f)-(pic!!.height/2f))
        Gdx.input.isCatchBackKey = true
        GameLoader {
            val context = Context()
            context.bindSingleton(this@RPS)
            context.bindSingleton(Art(it.load<TextureAtlas>("art/packed_art.atlas")))
            context.bindSingleton(disp!!)
            context.bindSingleton(Font(it).load())
            context.bindSingleton(Sounds(it).load())
            context.bindSingleton(bridge)
            context.bindSingleton(iap)
            context.bindSingleton(SaveUtils.loadSave())
            context
        }.run {
            val app = App(it)
            if(!app.save.removeAds){
                bridge.loadAd()
            }
            loaded = true
            app.bridge.reportAnalytic(Analytics.Category.gameplay, Analytics.Action.newSession)
            if (flavour == FLAVOUR_DEV) {
//                changeScreenTo(SecretsScreen(app){
//                    changeScreenToAndDispose(GameScreen(app),it)
//                })
                changeScreenTo(MainScreen(app))
//                changeScreenTo(SecretsScreen(app, {
//                    changeScreenTo(GameScreen(app))
//                }))
            } else {
                changeScreenTo(MainScreen(app))

            }
        }
        ShaderProgram.pedantic = false
        whiteShader = ShaderProgram(vert, frag)

    }

    override fun render() {
        if(!loaded){
            Gdx.gl.glClearColor(1f,1f,1f,1f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
            if(batch == null) return
            if(Gdx.app.type == Application.ApplicationType.iOS) return
            batch!!.begin()
            pic!!.draw(batch,1f)
            batch!!.end()
        }else{
            super.render()
        }

    }
    fun changeScreenTo(newScreen: KtxScreen) {
        val oldScreen = screens.get(newScreen.javaClass)
        if (oldScreen != null) {
            if (oldScreen == newScreen) {
                setScreen(newScreen.javaClass)
                return
            }
            oldScreen.dispose()
            removeScreen((newScreen).javaClass)
        }
        addScreen(newScreen.javaClass, newScreen)
        setScreen(newScreen.javaClass)
    }


}

const val FLAVOUR_DEV = "dev"
const val FLAVOUR_RELEASE = "release"
