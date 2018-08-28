package com.davjhan.hangdx

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Align


/**
 * Created by david on 2017-12-22.
 *
 */
data class Disp(private val displayWidth: Float, private val displayHeight: Float) {
    companion object {
        val stageWidth: Float = 270f
        val stageHeight: Float = 480f
    }

    val screenWidth: Float
    val screenHeight: Float
    val letterbox: Vector2
    val displayRatio = displayHeight / displayWidth
    val ppi: Float
    val xScale: Float
    val yScale: Float

    init {
        info()
        if (displayRatio >= 16f / 9) {
            screenHeight = stageWidth * displayRatio
            screenWidth = screenHeight / displayRatio
        } else {

            screenHeight = stageHeight
            screenWidth = stageHeight / displayRatio


        }
        letterbox = Vector2((screenWidth - stageWidth) / 2, (screenHeight - stageHeight) / 2)
        ppi = displayWidth / stageWidth

        xScale = displayWidth / screenWidth
        yScale = displayHeight / screenHeight
    }

    fun windowToStage(point: Vector2): Vector2 {
        return point
    }

    fun info(): String {
        return "stage (w: ${screenWidth}, h: ${screenHeight})\n screen w: ${screenWidth}, h: ${screenHeight}"
    }

    fun position(alignment: Int, inset: Float = 0f): Vector2 {
        return when (alignment) {
            Align.bottomLeft -> Vector2(inset, inset)
            Align.bottom -> Vector2(hScreenWidth, inset)
            Align.bottomRight -> Vector2(screenWidth - inset, inset)
            Align.left -> Vector2(inset, hScreenHeight)
            Align.center -> Vector2(hScreenWidth, hScreenHeight)
            Align.right -> Vector2(screenWidth - inset, hScreenHeight)
            Align.topLeft -> Vector2(inset, screenHeight - inset)
            Align.top -> Vector2(hScreenWidth, screenHeight - inset)
            Align.topRight -> Vector2(screenWidth - inset, screenHeight - inset)
            else -> {
                Vector2(0f, 0f)
            }
        }
    }

    val hScreenWidth: Float
        get() = screenWidth / 2

    val hScreenHeight: Float
        get() = screenHeight / 2
}