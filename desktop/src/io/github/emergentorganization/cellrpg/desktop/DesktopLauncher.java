package io.github.emergentorganization.cellrpg.desktop;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglFileHandle;
import com.badlogic.gdx.backends.lwjgl.LwjglPreferences;
import io.github.emergentorganization.cellrpg.PixelonTransmission;
import io.github.emergentorganization.cellrpg.tools.saves.GameSettings;

import java.awt.*;
import java.io.File;

public class DesktopLauncher {

	private static Preferences getPreferences(LwjglApplicationConfiguration config)
	{
		return new LwjglPreferences(new LwjglFileHandle(
				new File(config.preferencesDirectory, GameSettings.CONFIG_FILE),
				config.preferencesFileType
		));
	}

	private static void configureWindow(Preferences prefs, LwjglApplicationConfiguration config)
	{
		String type = prefs.getString(GameSettings.KEY_GRAPHICS_TYPE, "windowed");
		int width = prefs.getInteger(GameSettings.KEY_GRAPHICS_WIDTH, 0);
		int height = prefs.getInteger(GameSettings.KEY_GRAPHICS_HEIGHT, 0);

		System.out.println("Configuring window: \n" + "width:" + width+ "\nheight:" + height +"\ntype:" + type);

		if(type.equals("windowed"))
		{
			if(width == 0 || height == 0)
			{
				System.out.println("Invalid window size");

				config.width = 600;
				config.height = 400;

				prefs.putInteger(GameSettings.KEY_GRAPHICS_WIDTH, config.width);
				prefs.putInteger(GameSettings.KEY_GRAPHICS_HEIGHT, config.height);
			}else{
				config.width = width;
				config.height = height;
			}

		}else if(type.equals("fullscreen-windowed"))
		{
			System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");

			Rectangle maxWindowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

			config.x = config.y = 0;
			config.width = maxWindowBounds.width;
			config.height = maxWindowBounds.height;
		}else if(type.equals("fullscreen"))
		{
			config.fullscreen = true;

			Graphics.DisplayMode dm = config.getDesktopDisplayMode();
			config.width = dm.width;
			config.height = dm.height;
		}

		System.out.println(config.width +" , " + config.height);
		prefs.putString(GameSettings.KEY_GRAPHICS_TYPE, type);
		prefs.flush();
	}


	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		Preferences prefs = getPreferences(config);

		configureWindow(prefs, config);
		config.resizable = false;

		config.useGL30 = true;

		System.out.println("initializing application...");

		GameSettings.setPreferences(prefs);
		new LwjglApplication(new PixelonTransmission(), config);
	}
}
