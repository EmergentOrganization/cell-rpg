package io.github.emergentorganization.cellrpg.tools.saves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;


public class LoadoutSave {
    // json string with equipment details
    public static String KEY_EQUIPMENT_JSON = "equipment-loadout-json";

    // player preferences file:
    public static String PREFS_FILE_MIDFIX = ".load";

    // cached preferences file
    private static IntMap<Preferences> prefs = new IntMap<Preferences>();

    public static Preferences getPreferences(String gameSavePrefix, int loadoutNumber) {
        if(prefs.containsKey(loadoutNumber)){
            return prefs.get(loadoutNumber);
        } else {
            prefs.put(loadoutNumber, Gdx.app.getPreferences(gameSavePrefix + PREFS_FILE_MIDFIX + loadoutNumber));
            return prefs.get(loadoutNumber);
        }
    }

    public static void dispose(){
        for (Preferences pref : prefs.values()){
            pref.flush();
        }
    }

    public static Json getLoadoutJson(String gameSavePrefix, int loadoutNumber){
        // returns json obj loaded from file
        Preferences loadPrefs = getPreferences(gameSavePrefix, loadoutNumber);
        String jsonLoadout = loadPrefs.getString(KEY_EQUIPMENT_JSON, "[]");
        Json json = new Json();
        json.toJson(jsonLoadout);
        return json;
    }
}
