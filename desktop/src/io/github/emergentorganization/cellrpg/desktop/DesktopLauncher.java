package io.github.emergentorganization.cellrpg.desktop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
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

		Preferences prefs = GameSettings.getPreferences();

		// Graphics.DisplayMode desktop = LwjglApplicationConfiguration.getDesktopDisplayMode();
		int ww = prefs.getInteger(GameSettings.KEY_GRAPHICS_WIDTH, 1024);
		int wh = prefs.getInteger(GameSettings.KEY_GRAPHICS_HEIGHT, 768);
		boolean fs = prefs.getBoolean(GameSettings.KEY_GRAPHICS_FULLSCREEN, false);

		System.out.println("Resizing: " + ww +", " + wh + ". Fullscreen: " + fs);

		Gdx.graphics.setDisplayMode(
				ww,
				wh,
				fs // fullscreen kills performance? :( why???
		);
	}
}
