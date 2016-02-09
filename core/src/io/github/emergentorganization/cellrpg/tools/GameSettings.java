package io.github.emergentorganization.cellrpg.tools;

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
    public static String KEY_CAM_FOLLOW_METHOD = "camera-follow-method";

    // min px between player & screen edge
    public static String KEY_CAM_EDGE_MARGIN = "Cam_EdgeMargin";
    // dist camera should try to lead player movement
    public static String KEY_CAM_LEAD = "Cam_Lead";
    // min distance between player & cam we care about (to reduce small-dist jitter & performance++)
    public static String KEY_CAM_NEARNESS_CUTOFF = "Cam_NearnessCutoff";

    // === OTHER ===
    public static String USER_ID = "user_id";
    public static String KEY_DEV_DEVMODE = "DevMode"; // Naming syntax is TYPE_CATEGORY_NAME

    private static String PREFS_FILE = "io.github.emergentorganization.cellrpg.configuration";

    public static Preferences getPreferences() {
        return Gdx.app.getPreferences(PREFS_FILE);
    }

    public static boolean devMode(){
        return Gdx.app.getPreferences(PREFS_FILE).getBoolean(KEY_DEV_DEVMODE, false);
    }
}
