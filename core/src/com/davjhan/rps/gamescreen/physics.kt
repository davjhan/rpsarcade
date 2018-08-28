package com.davjhan.rps.gamescreen

import com.badlogic.gdx.utils.Align

fun handleCollision(characters:List<GameObject>){

    val collideables = characters.filter { it.isCollidable() }
    for(a in collideables){
        val others = collideables.filter { it != a }.toMutableList()



        while(true){
            val closest = others.minBy {
                it.position(Align.bottom).sub(a.position(Align.bottom)).len2()
            } ?: break
            val delta = closest.position(Align.bottom).sub(a.position(Align.bottom))

            if(delta.len() >= a.radius+closest.radius ){
                break
            }
            val offset = delta.cpy()
            offset.setLength((a.radius+closest.radius-delta.len())/2)
            closest.moveBy(offset.x,offset.y)
            a.moveBy(-offset.x,-offset.y)
            closest.handleCollision(a)
            a.handleCollision(closest)

            others.remove(closest)
        }

    }
}