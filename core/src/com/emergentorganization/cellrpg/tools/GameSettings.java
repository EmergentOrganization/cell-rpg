package com.emergentorganization.cellrpg.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;


public class GameSettings {
    // === MOVEMENT CONTROLS ===
    public static String KEY_MOVEMENT_CONTROL_METHOD = "movement-ctrl-method";

    // === WEAPON CONTROLS ===
    public static String KEY_WEAPON_CONTROL_METHOD = "weapon-ctrl-method";

    public static String KEY_WEAPON_PATHDRAW_RADIUS = "pathDraw-radius";
    public static String KEY_WEAPON_PATHDRAW_AUTOWALK = "pathDraw-autowalk";

    public static String KEY_WEAPON_CLICKSHOOT_RADIUS = "clickShoot-exclusion-radius";

    // === CAMERA CONTROLS ===
    public static String KEY_CAM_EDGE_MARGIN = "Cam_EdgeMargin";
    public static String KEY_CAM_LEAD = "Cam_Lead";
    public static String KEY_CAM_NEARNESS_CUTOFF = "Cam_NearnessCutoff";

    // === OTHER ===
    public static String USER_ID = "user_id";
    private static String PREFS_FILE = "com.emergentorganization.cellrpg.configuration";
    public static String KEY_DEV_DEVMODE = "DevMode"; // Naming syntax is TYPE_CATEGORY_NAME
    private boolean devModeEnabled = false;

    public static Preferences getPreferences() {
        return Gdx.app.getPreferences(PREFS_FILE);
    }
}
