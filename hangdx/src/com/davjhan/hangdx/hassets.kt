package com.davjhan.hangdx

import com.badlogic.gdx.graphics.Color
import ktx.async.assets.AssetStorage
import ktx.async.enableKtxCoroutines
import ktx.async.ktxAsync
import ktx.freetype.async.registerFreeTypeFontLoaders
import ktx.inject.Context

/**
 * Created by david on 2018-02-05.
 */
//https://coolors.co/e63946-f1faee-a8dadc-457b9d-1d3557
//object Colr {
//    val bg by lazy { Color.valueOf("#31a2f2") }
//    val black by lazy { Color.valueOf("#31a2f2") }
//    val ink by lazy { Color.valueOf("#111111") }
//    val lightInk by lazy { Color.valueOf("#457B9D") }
//    val cream by lazy { Color.valueOf("#F1FAEE") }
//    val accent by lazy { Color.valueOf("#E63946") }
//    val gooseYellow by lazy { Color.valueOf("#fbc624") }
//}
class GameLoader(val initLoaders: suspend (AssetStorage) -> Context) {

    fun run(onDone: (Context) -> Unit) {
        enableKtxCoroutines(asynchronousExecutorConcurrencyLevel = 1)
        val assetStorage = AssetStorage()

        assetStorage.registerFreeTypeFontLoaders()
        ktxAsync {
            assetStorage.apply {
                val context = initLoaders(assetStorage)
                onDone(context)
            }
        }
    }
}

open class HColr {
    open val dim by lazy { Color.valueOf("#31a2f2") }
}

val hColor: HColr = HColr()
