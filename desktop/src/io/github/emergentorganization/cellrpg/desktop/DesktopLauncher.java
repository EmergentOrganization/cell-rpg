package io.github.emergentorganization.cellrpg.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import io.github.emergentorganization.cellrpg.tools.GameSettings;
import io.github.emergentorganization.emergent2dcore.PixelonTransmission;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width  = 100; //LwjglApplicationConfiguration.getDesktopDisplayMode().width;
		config.height = 100; //LwjglApplicationConfiguration.getDesktopDisplayMode().height;
//		config.vSyncEnabled = false;
		config.useGL30 = true;
		new LwjglApplication(new PixelonTransmission(), config);

		Preferences prefs = GameSettings.getPreferences();

//		// uncomment and use this to set your preferred config for now: (TODO: graphics settings menu)
//		prefs.putInteger(GameSettings.KEY_GRAPHICS_WIDTH, 1300);
//		prefs.putInteger(GameSettings.KEY_GRAPHICS_HEIGHT, 400);
//		prefs.flush();

		Gdx.graphics.setDisplayMode(
				prefs.getInteger(GameSettings.KEY_GRAPHICS_WIDTH,  LwjglApplicationConfiguration.getDesktopDisplayMode().width),
				prefs.getInteger(GameSettings.KEY_GRAPHICS_HEIGHT, LwjglApplicationConfiguration.getDesktopDisplayMode().height),
				prefs.getBoolean(GameSettings.KEY_GRAPHICS_FULLSCREEN, false) // fullscreen kills performance? :( why???
		 );
	}
}
