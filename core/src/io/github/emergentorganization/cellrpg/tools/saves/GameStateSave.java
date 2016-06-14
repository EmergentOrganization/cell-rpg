package io.github.emergentorganization.cellrpg.tools.saves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;


public class GameStateSave {
    public static String KEY_SELECTED_LOADOUT = "selected-equipment-loadout-index";

    // player preferences file:
    public static String GAMESAVE_SUFFIX = "gamestate";

    // cached preferences file
    private static Preferences prefs;
    private static String prefs_user;

    public static Preferences getPreferences() {
        String user = GameSettings.getPreferences().getString(GameSettings.KEY_USER_NAME, GameSettings.DEF_USER_NAME);
        if (prefs_user != user || prefs == null){  // if user unchanged, use cached
            prefs = Gdx.app.getPreferences( GameSettings.getPrefsFilePrefix() + GAMESAVE_SUFFIX);
        }
        return prefs;
    }

    public static void dispose(){
        prefs.flush();
    }
}
