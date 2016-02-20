package io.github.emergentorganization.cellrpg.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import io.github.emergentorganization.cellrpg.tools.GameSettings;
import io.github.emergentorganization.emergent2dcore.PixelonTransmission;

public class DesktopLauncher {
	public static void main (String[] arg) {
		System.out.println("intializing configuration...");
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width  = 100;
		config.height = 100;
//		config.vSyncEnabled = false;
		config.useGL30 = true;
		System.out.println("initializing application...");
		new LwjglApplication(new PixelonTransmission(), config);

		System.out.println("loading prefs...");
		Preferences prefs = GameSettings.getPreferences();

//		// uncomment and use this to set your preferred config for now: (TODO: graphics settings menu)
//		prefs.putInteger(GameSettings.KEY_GRAPHICS_WIDTH, 1300);
//		prefs.putInteger(GameSettings.KEY_GRAPHICS_HEIGHT, 400);
//		prefs.flush();
		System.out.println("getting screen prefs...");
		int w = prefs.getInteger(GameSettings.KEY_GRAPHICS_WIDTH, LwjglApplicationConfiguration.getDesktopDisplayMode().width);
		int h = prefs.getInteger(GameSettings.KEY_GRAPHICS_HEIGHT, LwjglApplicationConfiguration.getDesktopDisplayMode().height);
		boolean fs = prefs.getBoolean(GameSettings.KEY_GRAPHICS_FULLSCREEN, false);

		System.out.println("resizing screen to " + w + "x" + h + ", fullscreen=" + fs);
		Gdx.graphics.setDisplayMode(
				w,
				h,
				fs // fullscreen kills performance? :( why???
		 );
	}
}
