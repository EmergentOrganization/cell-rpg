package com.emergentorganization.cellrpg.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Created by BrianErikson on 9/15/15.
 */
public class Config {
    private static String PREFS_FILE = "Configuration";
    private static Preferences prefs = Gdx.app.getPreferences(PREFS_FILE);

    public class Engine {

    }

    public class Gameplay {

    }

    public static class Development {
        public static String KEY_DEVMODE = "DevMode";

        public static void setDevMode(boolean on) {
            prefs.putBoolean(KEY_DEVMODE, on);
        }

        public static boolean getDevMode() {
            return prefs.getBoolean(KEY_DEVMODE);
        }
    }
}
