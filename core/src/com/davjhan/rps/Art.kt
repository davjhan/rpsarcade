package com.davjhan.rps

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.davjhan.hangdx.ArtBase

class Art(atlas: TextureAtlas) : ArtBase() {
    val bg = BG(cutNinesGroup(atlas.findRegion("ninepatches").split(32, 32), 12))
    val bigNines = BigNines(cutNinesGroup(atlas.findRegion("bigNines").split(48, 48), 16))
    val hand = atlas.findRegion("hand").split(36, 42).flatten()
    val title = atlas.findRegion("title")
    val gameover = atlas.findRegion("gameover").split(220, 48).flatten()
    val newBadge = atlas.findRegion("newBadge")
    val handArrow = atlas.findRegion("handArrow").split(40, 38).flatten()
    val characters = atlas.findRegion("characters").split(40, 50)
    val bigCharacters = atlas.findRegion("bigCharacters").split(48, 62)
    val bgTile = atlas.findRegion("bgTile").split(64, 64).flatten()
    val keypadIcons = atlas.findRegion("keypadicons").split(30, 30).flatten()
    val particles = atlas.findRegion("particles").split(14, 14)
    val flags = atlas.findRegion("playButtonIcon")
    val newGameIcon = atlas.findRegion("newGameIcon")
    val smallConfetti = atlas.findRegion("smallconfetti").split(8, 8)
    val bigConfetti = atlas.findRegion("bigconfetti").split(16, 16)
    val tinyIcons = atlas.findRegion("tinyIcons").split(16,24).flatten()
    val largeIcons = atlas.findRegion("largeIcons").split(30,32).flatten()
    val tinyProgressBar = cutNines(atlas.findRegion("tinyprogressbar").split(12,8),2,2,2,4)
    val medIcons = atlas.findRegion("medIcons").split(22,24).flatten()
    val secretIcons = atlas.findRegion("secretIcons").split(48,48).flatten()
    val vignette = cutNines(atlas.findRegion("vignette").split(32,32),15)[0]
    val characterCrowns = atlas.findRegion("charactercrowns").split(18,24).flatten()
}

class BigNines(nines: List<List<NinePatch>>) {
    val paper = NinePatchDrawable(nines[0][0])
    val bigButton = NinePatchDrawable(nines[0][1])
    val griditem =  NinePatchDrawable(nines[0][1])
    val gridItemGold =  NinePatchDrawable(nines[0][2])
    val griditemWhite =  NinePatchDrawable(nines[0][3])
    val textBG =  NinePatchDrawable(nines[0][4])
}

class BG(nines: List<List<NinePatch>>) {
    val default = NinePatchDrawable(nines[0][0])
    val primaryButtion = nines[0].slice(0..1).map { NinePatchDrawable(it) }
    val redButton = nines[1].slice(0..1).map { NinePatchDrawable(it) }
    val darkBlueButton = nines[2].slice(0..1).map { NinePatchDrawable(it) }
    val yellowButton: List<NinePatchDrawable>  =nines[3].slice(0..1).map { NinePatchDrawable(it) }
    val greenButton = nines[4].slice(0..1).map { NinePatchDrawable(it) }
    //    val ink = NinePatchDrawable(nines[1])
    //    val white = NinePatchDrawable(nines[3])
    //    val outlineInk = NinePatchDrawable(nines[4])
}

class LargeIcons(reg: List<TextureRegion>) {
    val leftArrow = reg[0]
    val rightArrow = reg[1]
    val x = reg[2]
    val settings = reg[3]
}

class MedIcons(reg: List<TextureRegion>) {
    val rightArrow = reg[0]
    val leftArrow = reg[1]
    val settings = reg[2]
}
