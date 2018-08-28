package com.davjhan.rps

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.davjhan.hangdx.Disp
import com.davjhan.hangdx.HColr
import ktx.async.assets.AssetStorage

/**
 * Created by david on 2018-02-05.
 */
class Colr : HColr() {
    override val dim by lazy { Color.valueOf("#00000099") }
    val superdim by lazy { Color.valueOf("#000000c8") }
    val shadow by lazy { Color.valueOf("#00000064") }
    val bg by lazy { Color.valueOf("#31a2f2") }
    val black by lazy { Color.valueOf("#0a0a0a") }
    val ink by lazy { Color.valueOf("#0b132b") }
    val darkBg by lazy { Color.valueOf("#18244a") }
    val dimWhite by lazy { Color.valueOf("#fefefec4") }
    val lightInk by lazy { Color.valueOf("#457B9D") }
    val white by lazy { Color.valueOf("#eef9fa") }
    val grey by lazy { Color.valueOf("#ababab") }
    val green by lazy { Color.valueOf("#c3de46") }
    val greenDark by lazy { Color.valueOf("#81ad51") }
    val greyDark by lazy { Color.valueOf("#555555") }
    val lig by lazy { Color.valueOf("#FEFEFE") }
    val red by lazy { Color.valueOf("#E63946") }
    val gold by lazy { Color.valueOf("#f3cc2b") }
    val goldDark by lazy { Color.valueOf("#bc881f") }
    val blue by lazy { Color.valueOf("#31A2F2") }
    val blueSecondary by lazy { Color.valueOf("#ADDDFF") }
}

val colr = Colr()

class Font(val store: AssetStorage) {
    val VCR = "font/VCR_OSD_MONO_1.001.ttf"
    val BODY_FONT = "font/Born2bSportyV2.ttf"
    val SUPERSTAR = "font/superstar.ttf"
    val CAPTION = "font/slkscr.ttf"
    val disp = Disp(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

    lateinit var c: BitmapFont
    lateinit var red1: BitmapFont
    lateinit var yellow1: BitmapFont
    lateinit var green1: BitmapFont
    lateinit var reg1: BitmapFont
    lateinit var reg2: BitmapFont
    lateinit var b1: BitmapFont
    lateinit var sub1: BitmapFont
    lateinit var sub2: BitmapFont
    lateinit var sub3: BitmapFont
    lateinit var b2: BitmapFont
    lateinit var keypad1: BitmapFont
    lateinit var keypad2: BitmapFont
    suspend fun load(): Font {
        val darkPurple = Color.valueOf("693773")
        c = loadFont(CAPTION) {
            color = colr.ink
//            borderWidth = 2f
//            borderColor = darkBg
            shadowOffsetY = 2
            shadowColor = colr.shadow
            size = 8

        }

        keypad1 = loadFont(SUPERSTAR) {
            color = colr.ink
            shadowOffsetY = 1
            shadowColor = Color.valueOf("d5b8d6")
            size = 16

        }
        keypad2 = loadFont(SUPERSTAR) {
            color = colr.ink
            shadowOffsetY = 2
            shadowColor = Color.WHITE
            size = 32

        }
        red1 = get3D(SUPERSTAR,
                Color.valueOf("fdbdc0"),
                Color.valueOf("65142b"), 16)
        yellow1 = get3D(SUPERSTAR,
                Color.valueOf("fdee9e"),
                Color.valueOf("763624"), 16)

        green1 = get3D(SUPERSTAR,
                Color.valueOf("e9ee9d"),
                Color.valueOf("1c5134"), 16)
        reg1 = getDef(16)
        reg2 = getDef(32)

        sub1 = getSub(16)
        sub2 = getSub(32)
        sub3 = getSub(48)

        b1 = loadFont(SUPERSTAR) {
            color = colr.ink
            size = 16
            shadowColor = colr.shadow
            shadowOffsetY = 1

        }
        b2 = loadFont(VCR) {
            color = colr.ink
            size = 40
            shadowColor = colr.shadow
            shadowOffsetY = 1
        }

        store.clear()

        return this
    }
    fun getDef(size: Int) = get3D(SUPERSTAR,
            colr.white,
            colr.ink, size)
    fun getSub(size: Int) = get3D(SUPERSTAR,
            Color.valueOf("a2b2fd"),
            colr.darkBg, size)

    fun get3D(
            font: String,
            _color: Color, shadow: Color, _size: Int) = loadFont(font) {
        color = _color
        borderWidth = 2f
        borderColor = shadow
        shadowOffsetY = 2
        shadowColor = shadow
        size = _size

    }

    private inline fun loadFont(
            file: String,
            setup: FreeTypeFontGenerator.FreeTypeFontParameter.() -> Unit = {}): BitmapFont {
        val params = FreeTypeFontGenerator.FreeTypeFontParameter()

        params.setup()
        params.mono = true
//        val regsize = params.size
//        params.size = (params.size * disp.xScale).toInt()
////        params.shadowOffsetX = (params.shadowOffsetX * disp.xScale).toInt()
////        params.shadowOffsetY = (params.shadowOffsetY * disp.yScale).toInt()
////        params.borderWidth *= disp.xScale
        val ret = FreeTypeFontGenerator(Gdx.files.internal(file)).generateFont(params)
//        ret.data.scaleX = 1 / disp.xScale
//        ret.data.scaleY = 1 / disp.yScale
//        ret.data.descent = 0f
//        ret.data.ascent = 0f
//        ret.data.capHeight = regsize.toFloat()*0.9f
//        ret.data.xHeight = regsize.toFloat()
//        ret.data.capHeight
        return ret
    }

}
