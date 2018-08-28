package com.davjhan.hangdx

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Timer


/**
 * Created by david on 2017-12-23.
 */

object pixUtils {
    fun solidRectDrawable(color: Color, width: Int = 1, height: Int = 1): TextureRegionDrawable = TextureRegionDrawable(solidRect(color, width, height))

    fun solidRect(color: Color, width: Int = 1, height: Int = 1): TextureRegion {
        val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)
        pixmap.blending = Pixmap.Blending.None
        pixmap.setColor(color)
        pixmap.fillRectangle(0, 0, width, height)
        val texture = Texture(pixmap)
        pixmap.dispose()
        return TextureRegion(texture)
    }

    fun linearDim(color: Color, width: Int = 1, height: Int = 1, inverted: Boolean = false): TextureRegion {
        val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)
        val originalAlpha = color.a
        pixmap.blending = Pixmap.Blending.None
        for (i in 0 until height) {
            val percent = i / height.toFloat()
            color.a = originalAlpha * (if (inverted) 1f - percent else percent)
            pixmap.setColor(color)
            pixmap.fillRectangle(0, i, width, 1)
        }
        val texture = Texture(pixmap)
        pixmap.dispose()
        return TextureRegion(texture)
    }

    fun solidCircle(color: Color, radius: Int = 1): TextureRegion {
        val pixmap = Pixmap(radius * 2, radius * 2, Pixmap.Format.RGBA8888)
        pixmap.setColor(color)
        pixmap.fillCircle(radius - 1, radius - 1, radius - 1)
        val texture = Texture(pixmap)
        pixmap.dispose()
        return TextureRegion(texture)
    }

    fun tintTexture(textureRegion: TextureRegion, tintColor: Color): TextureRegion {
        textureRegion.texture.textureData.prepare()
        val pixmap = textureRegion.texture.textureData.consumePixmap()
        val dupe = Pixmap(textureRegion.regionWidth, textureRegion.regionHeight, Pixmap.Format.RGBA8888)
        for (y in 0 until textureRegion.regionHeight) {
            for (x in 0 until textureRegion.regionWidth) {
                if (pixmap.getPixel(textureRegion.regionX + x, textureRegion.regionY + y) != 0) {
                    dupe.drawPixel(x, y, Color.rgba8888(tintColor))
                }

            }
        }
        val output = TextureRegion(Texture(dupe))
        textureRegion.texture.textureData.disposePixmap()
        dupe.dispose()
        return output
    }

    fun shadowBottom(textureRegion: TextureRegion, tintColor: Color): TextureRegion {
        textureRegion.texture.textureData.prepare()
        val pixmap = textureRegion.texture.textureData.consumePixmap()
        val dupe = Pixmap(textureRegion.regionWidth, textureRegion.regionHeight, Pixmap.Format.RGBA8888)
        for (y in 0 until textureRegion.regionHeight) {
            for (x in 0 until textureRegion.regionWidth) {
                if (pixmap.getPixel(textureRegion.regionX + x, textureRegion.regionY + y) != 0) {
                    dupe.drawPixel(x, y, Color.rgba8888(tintColor))
                }

            }
        }
        val output = TextureRegion(Texture(dupe))
        textureRegion.texture.textureData.disposePixmap()
        dupe.dispose()
        return output
    }
}

fun Timer.schedule(delay: Float, action: () -> Unit): Timer.Task {
    return scheduleTask(object : Timer.Task() {
        override fun run() {
            action()
        }
    }, delay)
}