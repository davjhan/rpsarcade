package com.davjhan.rps.secretscreen

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.davjhan.hangdx.HButton
import com.davjhan.hangdx.Size
import com.davjhan.hangdx.acts
import com.davjhan.hangdx.spawn
import com.davjhan.rps.*
import com.davjhan.rps.data.Secret
import com.davjhan.rps.othermenus.UiCommon.makeIconButton
import com.davjhan.rps.othermenus.UiCommon.makeSecretIcon
import ktx.actors.onClick

class SecretDetailPopup(app: App,val secret: Secret, val onRefresh:()->Unit) : Popup(app) {

    val icon = makeSecretIcon(app,secret)
    val descriptionBody = hlabel(app, "") {
        setWrap(true)
        setAlignment(Align.topLeft)
        var desc = secret.description
        if(secret.isLocked(app.save)){
            val shuffled = desc.toMutableList()
            shuffled.shuffle()
            desc = shuffled.joinToString("")
        }
        setText(desc)
    }
    val description = htable {
        pad(Size.reg)
        padBottom(Size.xreg)
        padTop(Size.lg)
        background = app.art.bigNines.textBG
        addDecal(hlabel(app, "How to Unlock:", app.font.sub1), Align.top, Align.center, yOffset = -4f)
        add(descriptionBody).pad(Size.sm).grow()
        pack()
    }
    val title = hlabel(app,"",_font = app.font.reg2)

    init {
        body.background = app.art.bigNines.paper

        body.addThenRow(htable {
            pad(0f)
            add(icon).width(80f).height(80f).space(Size.sm)
            add(wrappedLabel(app, hlabel(app, if(secret.isLocked(app.save))"Locked" else secret.name) { setWrap(true) }) {
                addDecal(hlabel(app, "Name", app.font.sub1), Align.top, Align.center, yOffset = -4f)
                background = app.art.bigNines.textBG

            }).grow()
        }).growX()
        body.addThenRow(description).grow()
        val actionButton: HButton

        if (secret.isLocked(app.save)) {
            title.setText("Locked")
            actionButton =  makeIconButton(app,app.art.largeIcons[6], "Decode ${if(app.save.removeAds) "" else "(Ad)"}") {
                onClick {
                    fun decode(){
                        descriptionBody.setText(secret.description)
                        this@makeIconButton.remove()
                    }
                    if(app.save.removeAds){
                        decode()
                    }else{
                        app.bridge.showAd {
                            decode()
                        }
                    }
                }
            }
            app.sounds.play(app,app.sounds.lockedOpen)
        } else if (secret.isEnabled(app.save)) {
            title.setText("Equipped")
            actionButton = makeIconButton(app,app.art.largeIcons[4], "Unequip") {
                setBackgrounds(app.art.bg.yellowButton)
                onClick {
                    secret.disable(app.save)
                    onRefresh()
                    val newPopup = SecretDetailPopup(app,secret,onRefresh)
                    stage.spawn(newPopup)
                    this@SecretDetailPopup.remove()
                }
            }
            app.sounds.play(app,app.sounds.unlockedOpen)
        } else {
            title.setText("Unlocked")
            actionButton = makeIconButton(app,app.art.largeIcons[5], "Equip") {
                onClick {
                    secret.enable(app.save)
                    app.sounds.play(app,app.sounds.snap)
                    onRefresh()
                    val newPopup = SecretDetailPopup(app,secret,onRefresh)
                    stage.spawn(newPopup)
                    this@SecretDetailPopup.remove()
                }
            }
            app.sounds.play(app,app.sounds.unlockedOpen)
        }
        actionButton.addAction(Actions.forever(acts.lookAtMe(0.3f,1.05f)))
        body.addThenRow(actionButton).space(Size.sm).growX()
//        body.add(htextbutton(app,"Back"){
//            onClick {
//                this@SecretDetailPopup.remove()
//            }
//        }).height(48f).growX()

        body.addAction(acts.lookAtMe(scaleUp = 1.06f))
        body.padBottom(Size.lg)
        body.padTop(Size.xl)


        body.pack()
        pack()
        body.addDecal(title,Align.top,Align.center,0f,-Size.sm)
    }

    override fun remove(): Boolean {

        secret.ack(app.save)
        return super.remove()
    }
}