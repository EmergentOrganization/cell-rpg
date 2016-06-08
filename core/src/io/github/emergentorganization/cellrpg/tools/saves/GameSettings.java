package io.github.emergentorganization.cellrpg.tools.saves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;


public class GameSettings {
    // === GRAPHICS SETTINGS ===
    public static final String KEY_GRAPHICS_WIDTH = "screen-size-width";
    public static final String KEY_GRAPHICS_HEIGHT = "screen-size-height";
    public static final String KEY_GRAPHICS_TYPE = "screen-size-type";

    // === MOVEMENT CONTROLS ===
    public static final String KEY_MOVEMENT_CONTROL_METHOD = "movement-ctrl-method";

    // === WEAPON CONTROLS ===
    public static final String KEY_WEAPON_CONTROL_METHOD = "weapon-ctrl-method";

    public static final String KEY_WEAPON_PATHDRAW_RADIUS = "pathDraw-radius";
    public static final String KEY_WEAPON_PATHDRAW_AUTOWALK = "pathDraw-autowalk";

    public static final String KEY_WEAPON_CLICKSHOOT_RADIUS = "clickShoot-exclusion-radius";

    // === CAMERA CONTROLS ===
    public static final String KEY_CAM_FOLLOW_METHOD = "camera-follow-method";

    // min px between player & screen edge
    public static final String KEY_CAM_EDGE_MARGIN = "Cam_EdgeMargin";
    // dist camera should try to lead player movement
    public static final String KEY_CAM_LEAD = "Cam_Lead";
    // min distance between player & cam we care about (to reduce small-dist jitter & performance++)
    public static final String KEY_CAM_NEARNESS_CUTOFF = "Cam_NearnessCutoff";

    // === Player Details ===
    public static final String KEY_USER_ID = "user-id";
    public static final String KEY_USER_NAME = "user-name";
    public static final String KEY_FIRST_START = "first-start";
    public static final String KEY_GAME_NUMBER = "game-save-file-number";

    // === OTHER ===
    public static final String KEY_DEV_DEVMODE = "DevMode"; // Naming syntax is TYPE_CATEGORY_NAME

    public static final String PREFS_FILE = "io.github.emergentorganization.cellrpg.configuration";

    // cached preferences file
    private static Preferences prefs;

    public static Preferences getPreferences() {
        if (prefs != null) {
            return prefs;
        }
        prefs = Gdx.app.getPreferences(PREFS_FILE);
        return prefs;
    }

    public static void setPreferences(Preferences prefs) {
        GameSettings.prefs = prefs;
    }

    public static boolean devMode() {

        return getPreferences().getBoolean(KEY_DEV_DEVMODE, false);
    }

    public static void dispose() {
        getPreferences().flush();
    }
}
