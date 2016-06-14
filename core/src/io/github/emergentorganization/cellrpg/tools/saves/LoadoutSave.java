package io.github.emergentorganization.cellrpg.tools.saves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import io.github.emergentorganization.cellrpg.core.entityfactory.Entities.Equipment.Equipment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;


public class LoadoutSave {
    // json string with equipment details
    public static String KEY_EQUIPMENT_JSON = "equipment-loadout-json";

    // player preferences file:
    public static String LOADOUT_SUFFIX = ".loadout.";

    // cached preferences files
    private static IntMap<Preferences> prefs = new IntMap<Preferences>();


    public static Preferences getPreferences(){
        int loadNumber = GameStateSave.getPreferences().getInteger(GameStateSave.KEY_SELECTED_LOADOUT, 0);
        return getPreferences(loadNumber);
    }
    public static Preferences getPreferences(int loadoutNumber) {
        if(!prefs.containsKey(loadoutNumber)){
            prefs.put(loadoutNumber, Gdx.app.getPreferences(GameSettings.getPrefsFilePrefix() + LOADOUT_SUFFIX + loadoutNumber));
        }
        return prefs.get(loadoutNumber);
    }

    public static void dispose(){
        for (Preferences pref : prefs.values()){
            pref.flush();
        }
    }

    public static void saveLoadout(ArrayList<Equipment> loadout){
        saveLoadout(selectedLoadout(), loadout);
    }

    public static void saveLoadout(int loadoutNumber, ArrayList<Equipment> loadout){
        Preferences pref = getPreferences(loadoutNumber);
        Json json = new Json();
        String equipmentStr = json.toJson(loadout, ArrayList.class);
        pref.putString(KEY_EQUIPMENT_JSON, equipmentStr);
        pref.flush();
        logger.info("loadout #" + loadoutNumber +"(n="+ loadout.size() + ") saved to file");
    }

    public static ArrayList<Equipment> getLoadout(){
        // returns json obj loaded from file
        Preferences loadPrefs = getPreferences();
        String jsonLoadout = loadPrefs.getString(KEY_EQUIPMENT_JSON, null);
        if (jsonLoadout == null){
            logger.info("loaded empty loadout");
            return null;
        } else {
            Json json = new Json();
            ArrayList<Equipment> eq = json.fromJson(ArrayList.class, jsonLoadout);
            logger.info(eq.size() + " equipment loaded from file");
            return eq;
        }
    }

    private static int selectedLoadout(){
        return GameStateSave.getPreferences().getInteger(GameStateSave.KEY_SELECTED_LOADOUT, 0);
    }

    private static final Logger logger = LogManager.getLogger(LoadoutSave.class);
}
