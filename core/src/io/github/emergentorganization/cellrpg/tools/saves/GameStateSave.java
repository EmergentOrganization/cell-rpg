package io.github.emergentorganization.cellrpg.tools.saves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.IntMap;


public class GameStateSave {
    public static String KEY_SELECTED_LOADOUT = "selected-equipment-loadout-index";

    // player preferences file:
    public static String PREFS_FILE_PREFIX = "io.github.emergentorganization.cellrpg.gamestate.save";

    // cached preferences file
    private static IntMap<Preferences> prefs = new IntMap<Preferences>();

    public static Preferences getPreferences(int gameNumber) {
        if(prefs.containsKey(gameNumber)){
            return prefs.get(gameNumber);
        } else {
            prefs.put(gameNumber, Gdx.app.getPreferences(getSaveFilePrefix(gameNumber)));
            return prefs.get(gameNumber);
        }
    }

    public static String getSaveFilePrefix(int gameNumber){
        return PREFS_FILE_PREFIX + gameNumber;
    }

    public static void dispose(){
        for (Preferences pref : prefs.values()){
            pref.flush();
        }
    }
}
