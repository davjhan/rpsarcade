package com.davjhan.hangdx

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion

/**
 * Created by david on 2018-02-05.
 */
open class ArtBase() {
    fun cutNines(arr: Array<Array<TextureRegion>>, left: Int,
                 right: Int = left,
                 top: Int = left,
                 bottom: Int = top): List<NinePatch> {
        val regs = arr.flatten()
        return regs.map { NinePatch(it, left, right, top, bottom) }

    }

    fun cutNinesGroup(arr: Array<Array<TextureRegion>>, left: Int,
                      right: Int = left,
                      top: Int = left,
                      bottom: Int = top): List<List<NinePatch>> {
        return arr.map { it.map { NinePatch(it, left, right, top, bottom) } }

    }
}