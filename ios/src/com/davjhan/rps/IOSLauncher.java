package com.davjhan.rps;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.badlogic.gdx.pay.ios.apple.PurchaseManageriOSApple;
import com.davjhan.rps.data.IAPImpl;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSErrorException;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.pods.firebase.core.FIRApp;
import org.robovm.pods.google.GGLContext;

import static com.davjhan.rps.RPSKt.FLAVOUR_RELEASE;

public class IOSLauncher extends IOSApplication.Delegate {
    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        FIRApp.configure();
        try {
            GGLContext.getSharedInstance().configure();
            System.out.println("Configured");
        } catch (NSErrorException e) {
            System.err.println("Error configuring the Google context: " + e.getError());
        }
        System.out.println("Configured services.");

        IOSBridge bridge = new IOSBridge();
        config.allowIpod = true;
        return new IOSApplication(new RPS(bridge,new IAPImpl(new PurchaseManageriOSApple()),FLAVOUR_RELEASE), config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }
}