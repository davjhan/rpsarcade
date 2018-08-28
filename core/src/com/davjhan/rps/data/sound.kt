package com.davjhan.rps.data

import com.badlogic.gdx.audio.Sound
import com.davjhan.rps.App
import ktx.async.assets.AssetStorage

class Sounds(val store: AssetStorage) {

    lateinit var test: Sound
    lateinit var buttonDown: Sound
    lateinit var buttonDownPop: Sound
    lateinit var buttonUp: Sound
    lateinit var buttonUpPop: Sound
    lateinit var stretch: Sound

    lateinit var pickup: List<Sound>
    lateinit var dropoff: Sound
    lateinit var converted: List<Sound>
    lateinit var wallhit: Sound
    lateinit var gameover: Sound
    lateinit var lastManDown: Sound
    lateinit var snap: Sound
    lateinit var fanfare: Sound
    lateinit var boxPop: Sound
    lateinit var lockedOpen: Sound
    lateinit var unlockedOpen: Sound
    lateinit var highscore: Sound


    suspend fun load(): Sounds {
        test = store.load("sound/card-flip2.mp3")
        buttonDown = store.load("sound/button_down.mp3")
        buttonUp = store.load("sound/button_up.mp3")
        buttonDownPop = store.load("sound/button_down-1.mp3")
        buttonUpPop = store.load("sound/button_up-1.mp3")
        stretch = store.load("sound/stretching.mp3")

        pickup = listOf(store.load("sound/rock_up.mp3"),
                store.load("sound/paper_up.mp3"),
                store.load("sound/scissors_up.mp3"))

        dropoff = store.load("sound/item_down.mp3")
        converted = listOf(store.load("sound/rock_convert_heuh.mp3"),
                store.load("sound/paper_convert_heuh.mp3"),
                store.load("sound/scissors_convert_heuh.mp3"))

        wallhit = store.load("sound/wall_hit.mp3")
        gameover = store.load("sound/game-over.mp3")
        lastManDown = store.load("sound/last_dead_guy.mp3")
        snap = store.load("sound/snap.mp3")
        fanfare = store.load("sound/fanfare.mp3")
        boxPop = store.load("sound/box_pop.mp3")
        lockedOpen = store.load("sound/locked_open.mp3")
        unlockedOpen = store.load("sound/unlocked_open.mp3")
        highscore = store.load("sound/high_score.mp3")
        return this
    }

    fun play(app: App, sound: Sound) {
        if (app.save.soundOn) {
            sound.play()
        }
    }

    fun stop(app: App, sound: Sound) {

        sound.stop()

    }

    fun loop(app: App, sound: Sound): Long {
        if (app.save.soundOn) {
            return sound.loop()
        }
        return -1
    }

    fun stopLoop(app: App, sound: Sound, loopId: Long? = null) {
        if (app.save.soundOn) {
            if (loopId != null) sound.stop(loopId) else sound.stop()
        }
    }

    fun stopAll(app: App) {

    }

}