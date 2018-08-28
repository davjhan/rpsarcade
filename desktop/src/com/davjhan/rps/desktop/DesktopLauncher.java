package com.davjhan.rps.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.davjhan.hangdx.Disp;
import com.davjhan.rps.RPS;
import com.davjhan.rps.data.DummyIAP;

import static com.davjhan.rps.RPSKt.FLAVOUR_DEV;
import static com.davjhan.rps.desktop.TexturePackerKt.packTextures;

public class DesktopLauncher {
    public static void main(String[] arg) {
        packTextures();
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        float scale = 1.5f;

        config.width = (int) (Disp.Companion.getStageWidth() * scale);
        config.height = (int) ((Disp.Companion.getStageHeight()+200) * scale);
//		config.height = (int) (558 * scale);
        new LwjglApplication(new RPS(new DesktopBridge(),new DummyIAP(),FLAVOUR_DEV), config);
    }
}
