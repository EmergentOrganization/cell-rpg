package io.github.emergentorganization.cellrpg.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import io.github.emergentorganization.emergent2dcore.PixelonTransmission;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1350;
		config.height = 700;
		config.useGL30 = true;
		new LwjglApplication(new PixelonTransmission(), config);
	}
}
