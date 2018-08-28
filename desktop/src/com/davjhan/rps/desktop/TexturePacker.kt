package com.davjhan.rps.desktop

import com.badlogic.gdx.tools.texturepacker.TexturePacker

/**
 * Created by david on 2017-12-21.
 */
fun packTextures() {
    val INPUT_DIR = "../preassets/"
    val OUTPUT_DIR = "./art/"
    val PACK_FILENAME = "packed_art"
    val settings: TexturePacker.Settings = TexturePacker.Settings()
    settings.duplicatePadding = true
    settings.paddingX = 4
    settings.paddingX = 4
    TexturePacker.processIfModified(settings, INPUT_DIR, OUTPUT_DIR, PACK_FILENAME)
}