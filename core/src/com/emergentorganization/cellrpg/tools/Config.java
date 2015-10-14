package com.emergentorganization.cellrpg.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Created by BrianErikson on 9/15/15.
 */
public class Config {
    private static String PREFS_FILE = "com.emergentorganization.cellrpg.configuration";
    public static String KEY_DEV_DEVMODE = "DevMode"; // Naming syntax is TYPE_CATEGORY_NAME
    public static String KEY_INPUT_METHOD = "InputMethod";
    private boolean initialized = false;
    private boolean devModeEnabled = false;
    private Preferences prefs;

    /**
     * Must be called outside of constructors due to the LibGDX lifecycle
     */
    public void initialize() {
        prefs = Gdx.app.getPreferences(PREFS_FILE);

        devModeEnabled = prefs.getBoolean(KEY_DEV_DEVMODE);

        initialized = true;
    }

    public void setDevMode(boolean on) {
        if (!initialized)
            throw new RuntimeException("Initialize Config before use");
        prefs.putBoolean(KEY_DEV_DEVMODE, on);
        prefs.flush();
        devModeEnabled = on;
    }

    public boolean isDevModeEnabled() {
        if (!initialized)
            throw new RuntimeException("Initialize Config before use");
        return devModeEnabled;
    }

    public Preferences getPreferences() {
        return prefs;
    }
}
