package com.davjhan.rps.gamescreen

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.davjhan.hangdx.SpriteActor

open class TiledActor(tex: TextureRegion,rows:Int,cols:Int): SpriteActor() {
    init {
        for(r in 0 until rows){
            for(c in 0 until cols){
                val sprite = Sprite(tex)
                sprite.x = c*sprite.width
                sprite.y = r*sprite.height
                sprites.add(sprite)
            }
        }
        setSize(cols*tex.regionWidth.toFloat(),rows*tex.regionHeight.toFloat())
    }
}
class PanningTiledActor(tex: TextureRegion,rows:Int,cols:Int,duration:Float): TiledActor(tex,rows+1,cols+1) {
    init {
        addAction(Actions.forever(
                Actions.sequence(
                        Actions.moveBy(-tex.regionWidth.toFloat(),-tex.regionHeight.toFloat(),duration),
                        Actions.moveBy(tex.regionWidth.toFloat(),tex.regionHeight.toFloat())

                )
        ))
        touchable = Touchable.enabled
    }
}