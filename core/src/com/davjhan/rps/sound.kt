package com.davjhan.rps

import com.badlogic.gdx.Gdx

object Vibrate{
    fun short(app:App){
        if(app.save.vibrateOn) Gdx.app.input.vibrate(10)
    }
}