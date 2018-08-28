package com.davjhan.rps.data

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.davjhan.hangdx.BaseScreen
import com.davjhan.hangdx.spawn
import com.davjhan.rps.App
import com.davjhan.rps.GameScreen
import com.davjhan.rps.gamescreen.King
import com.davjhan.rps.othermenus.UiCommon

open class Secret(
        val id: String,
        val type: Int,
        val name: String,
        val description: String,
        val icon: (app: App) -> TextureRegion,
        val isEnabled: (save: Save) -> Boolean,
        val enable: (save: Save) -> Unit,
        val disable: (save: Save) -> Unit,
        private val testForUnlock: (data: GameScreen) -> Boolean) {
    fun isLocked(save: Save) = !save.unlockedSecrets.contains(id)
    fun isAcked(save: Save) = save.ackedSecrets.contains(id)
    fun canBeUnlocked(save: Save, data: GameScreen): Boolean = isLocked(save) && testForUnlock(data)
    fun unlock(app: App): Boolean {
        if (isLocked(app.save)) {
            app.save.changeValues {
                it.unlockedSecrets.add(id)
                it.hasNewSecrets = true
            }
            app.bridge.reportAnalytic(Analytics.Category.secrets,
                    Analytics.Action.secretUnlocked, id)
            return true
        } else {
            return false
        }
    }

    fun ack(save: Save) = if (!isAcked(save)) save.changeValues {
        it.ackedSecrets.add(id)
    } else {
    }
}


class SimpleSecret(
        type: Int,
        index: Int,
        name: String,
        description: String) :
        Secret(ObjType.idPrefixes[type].plus(index),
                type,
                name,
                description,
                { it.art.characters[type][index] },
                { it.selectedSkin[type] == index },
                { it.changeValues { it.selectedSkin[type] = index } },
                { it.changeValues { it.selectedSkin[type] = 0 } },
                { true })

class GameSecret(
        type: Int,
        index: Int,
        name: String,
        description: String,
        testForUnlock: (data: GameScreen) -> Boolean) :
        Secret(ObjType.idPrefixes[type].plus(index),
                type,
                name,
                description,
                { it.art.characters[type][index] },
                { it.selectedSkin[type] == index },
                { save -> save.changeValues { it.selectedSkin[type] = index } },
                { save -> save.changeValues { it.selectedSkin[type] = 0 } },
                testForUnlock)

object Secrets {

    val secrets = listOf<Secret>(

            GameSecret(
                    ObjType.ROCK, 1,
                    "Gold Rock",
                    """
                    Play 3 games.
                    """.trimIndent()) {
                it.app.save.gamesPlayed >= 3
            },

            GameSecret(
                    ObjType.PAPER, 1,
                    "Gold Paper",
                    """
                    Score over 25 points.
                    """.trimIndent()) {
                it.data.score >= 25
            },

            GameSecret(
                    ObjType.SCISSORS, 1,
                    "Gold Scissors",
                    """
                    Play a game with a skin equipped.
                    """.trimIndent()) { game ->
                !game.app.save.selectedSkin.all { it == 0 }
            },

            GameSecret(
                    ObjType.ROCK, 2,
                    "Brick",
                    """
                    End a game with more than 25 rocks on the field.
                    """.trimIndent()) { game ->
                game.characters.filter { it.type == ObjType.ROCK }.size >= 25
            },

            GameSecret(
                    ObjType.PAPER, 2,
                    "Toilet Paper",
                    """
                    End a game with more than 25 papers on the field.
                    """.trimIndent()) { game ->
                game.characters.filter { it.type == ObjType.PAPER }.size >= 25
            },

            GameSecret(
                    ObjType.SCISSORS, 2,
                    "Sword",
                    """
                    End a game with more than 25 scissors on the field.
                    """.trimIndent()) { game ->
                game.characters.filter { it.type == ObjType.SCISSORS }.size >= 25
            },

            GameSecret(
                    ObjType.ROCK, 3,
                    "Copper",
                    """
                    Hold a unit for 10 seconds.
                    """.trimIndent()) { game ->
                game.characters.any { it.analytics.longestPickedUpTime >= 10f }
            },

            GameSecret(
                    ObjType.PAPER, 3,
                    "Book",
                    """
                    Score over 25 points without converting a king.
                    """.trimIndent()) { game ->
                game.data.score >= 25
                        && game.characters.all { (it !is King) || it.analytics.convertedCount == 0 }
            },

            GameSecret(
                    ObjType.SCISSORS, 3,
                    "Shovel",
                    """
                    Play 25 games.
                    """.trimIndent()) {
                it.app.save.gamesPlayed >= 25
            },

            GameSecret(
                    ObjType.ROCK, 4,
                    "Emerald",
                    """
                    Fling a unit against the walls 25 times in one game.
                    """.trimIndent()) { game ->
                game.characters.any { it.analytics.hitWallCount >= 25 }
            },

            GameSecret(
                    ObjType.PAPER, 4,
                    "Sticky Pad",
                    """
                    Have 100 conversions in one game.
                    """.trimIndent()) { game ->
                game.characters.sumBy { it.analytics.convertedCount } >= 100
            },

            GameSecret(
                    ObjType.SCISSORS, 4,
                    "Fork",
                    """
                    Score over 30 points without picking up a king.
                    """.trimIndent()) { game ->
                game.data.score >= 30
                        && game.characters.all { (it !is King) || it.analytics.pickedUpCount == 0 }
            },


            GameSecret(
                    ObjType.ROCK, 5,
                    "Dice",
                    """
                    Score exactly 43 points with the gold rock, paper, and scissors set.
                    """.trimIndent()) { game ->
                game.data.score == 43
                        && game.app.save.selectedSkin.subList(0, 3).all { it == 1 }
            },

            SimpleSecret(
                    ObjType.PAPER, 5,
                    "Dollar Bill",
                    """
                    Remove Ads.
                    """.trimIndent()),

            GameSecret(
                    ObjType.SCISSORS, 5,
                    "Axe",
                    """
                    Play 100 games.
                    """.trimIndent()) {
                it.app.save.gamesPlayed >= 100
            },


            SimpleSecret(
                    ObjType.BOX, 1,
                    "Cardboard Box",
                    """
                    Tap the title in the title screen 100 times in a row.
                    """.trimIndent()),

            SimpleSecret(
                    ObjType.BOX, 2,
                    "Star Chest",
                    """
                    Do nothing for 5 minutes on the game over screen.
                    """.trimIndent()),
            GameSecret(
                    ObjType.BOX, 3,
                    "Briefcase",
                    """
                    Score over 60 points.
                    """.trimIndent()) {
                it.data.score >= 60
            },
            SimpleSecret(
                    ObjType.BOX, 4,
                    "Crate",
                    """
                    Wait 5 minutes before tapping the first box.
                    """.trimIndent()),
            GameSecret(ObjType.BOX, 5,
                    "Eye Box",
                    """
                    Pick up a unit 100 times in one game.
                    """.trimIndent()) { game ->
                game.characters.any { it.analytics.pickedUpCount >= 100 }
            }
    )

    fun isUnlocked(save: Save, id: String) = save.unlockedSecrets.contains(id)
    fun byId(id: String) = secrets.first { it.id == id }
    fun byId(type: Int, index: Int) = secrets.first { it.id == getId(type, index) }
    fun getId(type: Int, index: Int) = ObjType.idPrefixes[type].plus(index)
    fun onSecretUnlocked(app: App, screen: BaseScreen, secret: Secret) {
        screen.stage.spawn(UiCommon.makeSecretUnlockedNotification(app, secret) {
            app.controller.changeScreenTo(screen)
        })
        app.sounds.play(app, app.sounds.fanfare)
    }
}