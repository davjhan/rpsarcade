package com.davjhan.rps.secretscreen

import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.davjhan.hangdx.*
import com.davjhan.rps.*
import com.davjhan.rps.data.Secret
import com.davjhan.rps.data.Secrets
import com.davjhan.rps.gamescreen.PanningTiledActor
import com.davjhan.rps.gamescreen.createTapButon
import com.davjhan.rps.othermenus.UiCommon.makeRemoveAdsButton
import com.davjhan.rps.othermenus.UiCommon.makeSecretIcon
import ktx.actors.onClick

class SecretsScreen(val app: App, val onExit: () -> Unit, openSecret: Secret? = null) : BaseScreen(app.disp) {
    val downButton = htable {
        background = TextureRegionDrawable(pixUtils.linearDim(colr.dim, 1, 96))
        val icon = htable{
            add(hlabel(app,"Scroll Down"))
            row()
            add(HImage(app.art.largeIcons[3]))
            onClick {
                scrollpane.fling(0f, 0f, 0f)
                scrollpane.scrollTo(0f, 0f, 0f, 0f)

            }
            addListener(SquishyListener())
        }
        add(icon)
        icon.addAction(Actions.forever(acts.bob()))
        pWidth = disp.screenWidth
        pHeight = 96f

        pack()
    }
    var gameOver = htable {
        add(hlabel(app, "Game Over", app.font.reg2) {
            pad(Size.sm)
            setWrap(true)
            setAlignment(Align.center)
        })
    }
    val backButton = hiconbutton(app,app.art.largeIcons[0]) {
        onClick(onExit)
    }
    val tiles = mutableListOf<SecretTileItem>()
    val scrollBody = htable {
        padTop(56f)
        align(Align.top)
        Secrets.secrets.forEachIndexed { index, secret ->
            val item = SecretTileItem(app, secret) {
                val detailOverlay = SecretDetailPopup(app, it) {
                    tiles.forEach { it.refresh() }
                }
                showOverlay(detailOverlay)
            }
            tiles.add(item)
            add(item).growX().height(82f).space(Size.sm)
            if ((index + 1) % 3 == 0) row()
        }
        row()
        val removeAds = makeRemoveAdsButton(app,this@SecretsScreen)
        if(!app.save.removeAds) addThenRow(removeAds).colspan(3).fillX()

//        pack()

    }
    val scrollpane = hscrollpane(scrollBody) {
        touchable = Touchable.enabled

    }


    val particles: Group = Group()
    val bg = PanningTiledActor(app.art.bgTile[3], 11, 6, 4f)

    init {

        stage.addActor(bg)
        stage.spawn(scrollpane)
        stage.spawn(HImage(pixUtils.linearDim(colr.dim, disp.screenWidth.toInt(), 64,true)){
            touchable = Touchable.disabled
        },disp.position(Align.top),Align.top)
        stage.spawn(backButton, Size.reg, disp.screenHeight - Size.reg, Align.topLeft)

        stage.spawn(hlabel(app, "Secrets", app.font.reg1), disp.hScreenWidth, disp.screenHeight - Size.lg, Align.top)
        stage.spawn(downButton, disp.position(Align.bottom), Align.bottom)
        scrollpane.width = disp.screenWidth
        scrollpane.height = disp.screenHeight

        app.save.changeValues {
            it.hasNewSecrets = false
        }
        if (app.save.secretsFirstTime) {
            tiles[0].makeTutorialMode()
            scrollBody.swapActor(tiles[0], scrollBody.children.last())
            tiles[0].onClick {
                app.save.changeValues {
                    it.secretsFirstTime = false
                }
            }
        }
        if (openSecret != null) {
            val detailOverlay = SecretDetailPopup(app, openSecret) {
                tiles.forEach { it.refresh() }
            }
            showOverlay(detailOverlay)
        }
    }

    override fun onBackPressed() {
        onExit()
    }

    override fun update(delta: Float) {
        super.update(delta)
        downButton.isVisible = scrollpane.scrollY < scrollpane.maxY
    }

}

class SecretTileItem(val app: App, val secret: Secret, onClick: (secret: Secret) -> Unit) : HButton() {
    var tapButton: Actor? = null
    val lockedLabel = hlabel(app, "Locked")
    var icon: HTable = makeSecretIcon(app, secret)

    init {

        onClick {
            tapButton?.remove()
            onClick(secret)
        }
        pad(0f)
        padBottom(0f)
        add(icon).grow()
        refreshDecal()

    }

    fun refresh() {
        removeActor(icon)

        icon = makeSecretIcon(app, secret)
        addActorAt(0,icon)
        icon.pack()
        icon.setSize(width, height)
        refreshDecal()
    }

    private fun refreshDecal() {
        if (secret.isLocked(app.save)) {
            addDecal(lockedLabel, Align.center, Align.top, 0f, -12f)
        } else {
            removeDecal(lockedLabel)
            lockedLabel.remove()
        }
    }


    fun makeTutorialMode() {
        tapButton = createTapButon(app, "Tap!")
        addDecal(tapButton!!, Align.bottomRight, Align.center, -12f)
        addAction(Actions.forever(acts.lookAtMe(0.4f)))
        tapButton?.addAction(Actions.forever(acts.lookAtMe(0.4f)))
        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                clearActions()
                removeListener(this)
                return super.touchDown(event, x, y, pointer, button)
            }

        })

    }

}