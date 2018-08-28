package com.davjhan.rps

import com.badlogic.gdx.math.MathUtils.random

fun randRange(low: Int, high: Int) = random.nextInt(high - low) + low
fun randRange(low: Float, high: Float) = (random.nextFloat()*(high - low)) + low

class Clock(var resetTime:Float,val onEnd:()->Unit){

    constructor(resetTime:Float, once:Boolean, onEnd:()->Unit) : this(resetTime,onEnd) {
        this.once = once
    }
    var enabled:Boolean = true
    var once:Boolean = false
    var time:Float = resetTime
    fun update(delta:Float){
        if(!enabled) return
        time -= 0.016f
        if(time <= 0f && time + 0.016 > 0){
            time = if(once) 0f else resetTime
            onEnd()
        }
    }
    fun reset(){
        time = resetTime
    }
}