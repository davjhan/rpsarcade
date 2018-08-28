package com.davjhan.rps;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.pay.android.googleplay.AndroidGooglePlayPurchaseManager;
import com.davjhan.rps.data.IAPImpl;

import static com.davjhan.rps.RPSKt.FLAVOUR_RELEASE;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		System.out.println("Finished Launching");
		AndroidBridge bridge = new AndroidBridge(this);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;
		initialize(new com.davjhan.rps.RPS(bridge,
				new IAPImpl(new AndroidGooglePlayPurchaseManager(this, 0)),
				FLAVOUR_RELEASE), config);
	}
}
