package com.emergentorganization.cellrpg.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Created by BrianErikson on 9/15/15.
 */
public abstract class GameSettings {
    private static String PREFS_FILE = "com.emergentorganization.cellrpg.configuration";
    public static String KEY_DEV_DEVMODE = "DevMode"; // Naming syntax is TYPE_CATEGORY_NAME
    public static String KEY_INPUT_METHOD = "InputMethod";
    public static String KEY_CAM_EDGE_MARGIN = "Cam_EdgeMargin";
    public static String KEY_CAM_LEAD = "Cam_Lead";
    public static String KEY_CAM_NEARNESS_CUTOFF = "Cam_NearnessCutoff";
    public static String USER_ID = "user_id";
    private boolean devModeEnabled = false;
    private Preferences prefs;

    public static Preferences getPreferences() {
        return Gdx.app.getPreferences(PREFS_FILE);
    }
}
