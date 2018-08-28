package com.davjhan.rps.data

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter
import ktx.log.info

object SaveUtils {
    val json = Json()
    val filehandle = Gdx.app.files.local("rps_save")

    init {
        json.setTypeName(null)
        json.setIgnoreUnknownFields(true)
        json.setOutputType(JsonWriter.OutputType.json)
    }

    fun loadSave(): Save {
        val save: Save
        if (filehandle.exists()) {
            val jsonString = filehandle.readString()
            save = json.fromJson(Save::class.java, jsonString)
            info ("tttt [SaveUtils]"){"Loaded save."}
        }else{
            save = Save()
            info ("tttt [SaveUtils]"){"Created New Save."}
        }
        return save
    }

    fun save(save: Save) {
        filehandle.writeString(json.toJson(save), false)
    }
}

class Save {
    val version = 1
//    val unlockedSecrets = mutableListOf<String>()
    val unlockedSecrets = mutableListOf<String>()
    val ackedSecrets = mutableListOf<String>()
    val selectedSkin = mutableListOf<Int>(0, 0, 0,0)
    var highscore = 0
    var gamesPlayed = 0
    var removeAds:Boolean = false
    var gamesSinceLastAd:Int = 0
    var gameFirstTime = true
    var seenRatePopup = false
    var secretsFirstTime = true
    var graphicsHigh = true
    var soundOn = true
    var hasNewSecrets = false
    var vibrateOn = Gdx.app.type == Application.ApplicationType.Android

    fun changeValues(changes: (save: Save) -> Unit) {
        changes(this)
        SaveUtils.save(this)
    }
}
