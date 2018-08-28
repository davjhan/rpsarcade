package com.davjhan.rps

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Pool
import com.davjhan.hangdx.HApp
import com.davjhan.rps.data.*
import com.davjhan.rps.gamescreen.Particle
import com.davjhan.rps.gamescreen.ParticleType
import ktx.inject.Context

/**
 * Created by david on 2018-02-02.
 */
class App(context: Context) : HApp(context) {
    fun vibrate(duration: Int) {
        Gdx.input.vibrate(duration)
    }

    val art: Art = context.inject()
    val font: Font = context.inject()
    val controller: RPS = context.inject()
    var save: Save = context.inject()
    var iap: IAP = context.inject()
    var bridge: Bridge = context.inject()
    var sounds: Sounds = context.inject()
    val particlePools: MutableMap<ParticleType, Pool<Particle>> = mutableMapOf()

    init {
        for (type in ParticleType.values()) {
            particlePools.put(type, object : Pool<Particle>() {
                override fun newObject(): Particle {
                    return Particle(this@App, type)
                }

            })
        }
    }

    fun resetSaves() {
        save = Save()
        SaveUtils.save(save)
    }
}
