package com.davjhan.hangdx

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Affine2
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Disposable
import ktx.actors.alpha
import ktx.collections.toGdxArray
import net.dermetfan.gdx.graphics.g2d.AnimatedSprite

class WhiteShader() {
    //Make sure you call Shaderprogram.pendantic = false
    companion object {
        val frag = """
           #ifdef GL_ES
                #define LOWP lowp
                precision mediump float;
            #else
                #define LOWP
            #endif

            varying LOWP vec4 v_color;
            varying vec2 v_texCoords;
            uniform LOWP vec4 tint_color;
            uniform int tint_en;
            uniform sampler2D u_texture;
            //our screen resolution, set from Java whenever the display is resized
            uniform vec2 resolution;

            //RADIUS of our vignette, where 0.5 results in a circle fitting the screen
            const float RADIUS = 1.2;

            //softness of our vignette, between 0.0 and 1.0
            const float SOFTNESS = 1.0;

            void main(){

                LOWP vec4  texCol = texture2D(u_texture, v_texCoords);
                LOWP vec4 outCol;
                if(tint_en == 1){
                    outCol = mix(texCol,tint_color,tint_color.a);
                    outCol.a = texCol.a;
                }else{
                    outCol = texCol;
                }

               //determine center position
	            vec2 position = (gl_FragCoord.xy / resolution.xy) - vec2(0.5);

	            //determine the vector length of the center position
	            float len = length(position);

	            //use smoothstep to create a smooth vignette
	            float vignette = smoothstep(RADIUS, RADIUS - SOFTNESS, len);

	            //apply the vignette with 50% opacity
	            outCol.rgb = mix(outCol.rgb, outCol.rgb * vignette, 0.7);
                gl_FragColor = v_color* outCol;
            }

        """
        val vert = """
            attribute vec4 a_position;
            attribute vec4 a_color;
            attribute vec2 a_texCoord0;

            uniform mat4 u_projTrans;

            varying vec4 v_color;
            varying vec2 v_texCoords;

            void main() {
               v_color = a_color;
                v_texCoords = a_texCoord0;
                gl_Position = u_projTrans * a_position;
            }
        """
        lateinit var whiteShader: ShaderProgram
    }
}

open class SpriteActor() : SpriteBaseActor(), Tintable {

    override var tintColor: Color = Color(1f, 1f, 1f, 0.6f)
    override var tintEnabled: Boolean = false

    val sprites: MutableList<Sprite> = mutableListOf()
    open fun position(align: Int = Align.bottomLeft) = localToStageCoordinates(Vector2(getX(align), getY((align))))
    protected fun setSprite(sprite: Sprite) {
        sprites.add(sprite)
        setSize(sprite.width, sprite.height)
        setOrigin(Align.center)
    }

    protected fun setSprite(nine: NinePatch, width: Float, height: Float) {
        sprites.add(NinepatchSprite(nine, width, height))
        setSize(width, height)
        setOrigin(Align.center)
    }

    constructor(duration: Float, frames: List<TextureRegion>, mode: Animation.PlayMode = Animation.PlayMode.LOOP) : this() {
        sprites.add(AnimatedSprite(Animation<TextureRegion>(duration, frames.toGdxArray(), mode)))
        setSize(sprites[0].width, sprites[0].height)
        setOrigin(Align.center)
    }

    constructor(animatedSprite: AnimatedSprite) : this() {
        sprites.add(animatedSprite)
        setSize(animatedSprite.width, animatedSprite.height)
        setOrigin(Align.center)
    }

    constructor(textureRegion: TextureRegion) : this() {
        val sprite = Sprite(textureRegion)
        sprites.add(sprite)
        setSize(sprite.width, sprite.height)
        setOrigin(Align.center)
    }

    override fun drawInner(batch: Batch, parentAlpha: Float) {
        if (tintEnabled) {
            batch.shader.setUniformi("tint_en", 1)
            batch.shader.setUniformf("tint_color", tintColor)
            sprites.forEach {
                drawSprite(batch, it, parentAlpha)
            }
            batch.flush()
            batch.shader.setUniformi("tint_en", 0)
        } else {
            sprites.forEach { drawSprite(batch, it, parentAlpha) }
        }
    }

    override fun dispose() {
        sprites.forEach { it.texture.dispose() }
    }

    open fun addedToStage() {}
    override fun setStage(stage: Stage?) {
        if (stage != null) {
            addedToStage()
        }
        super.setStage(stage)
    }

    fun getPos(align: Int): Vector2 {
        return Vector2(getX(align), getY(align))
    }
}

abstract class SpriteBaseActor : Actor(), Disposable {
    private val worldTransform = Affine2()
    private val computedTransform = Matrix4()
    private val oldTransform = Matrix4()
    abstract fun drawInner(batch: Batch, parentAlpha: Float)
    open fun drawSprite(batch: Batch, sprite: Sprite, parentAlpha: Float) {

        if (sprite is AnimatedSprite) {
            sprite.update()
        }
        val oldScaleX = sprite.scaleX
        val oldScaleY = sprite.scaleY
        sprite.setScale(scaleX * sprite.scaleX, scaleY * sprite.scaleY)
        sprite.draw(batch, parentAlpha * alpha)
        sprite.setScale(oldScaleX, oldScaleY)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (batch == null) return
        applyTransform(batch, computeTransform())
        drawInner(batch, parentAlpha)
        resetTransform(batch)
    }

    private fun applyTransform(batch: Batch, transform: Matrix4) {
        oldTransform.set(batch.transformMatrix)
        batch.transformMatrix = transform
    }

    /** Restores the batch transform to what it was before [.applyTransform]. Note this causes the batch to
     * be flushed.  */
    private fun resetTransform(batch: Batch) {
        batch.transformMatrix = oldTransform

    }

    fun setPosition(pos: Vector2, alignment: Int) = setPosition(pos.x, pos.y, alignment)

    /** Returns the transform for this group's coordinate system.  */
    private fun computeTransform(): Matrix4 {

        val worldTransform = this.worldTransform
        val stagePos = parent.localToStageCoordinates(Vector2(x, y))
        worldTransform.setToTrnRotScl(stagePos.x, stagePos.y, rotation, 1f, 1f)
        computedTransform.set(worldTransform)
        return computedTransform
    }

}