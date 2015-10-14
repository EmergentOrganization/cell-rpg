package com.emergentorganization.cellrpg.tools;
import com.badlogic.gdx.Preferences;
import com.emergentorganization.cellrpg.CellRpg;

import java.util.UUID;

/**
 * Created by 7yl4r on 10/14/2015.
 */
public class UserIdentifier {
    private static String ID = null;
    private static boolean configLoaded = false;
    private static Preferences prefs;

    private static void loadFromConfig(){
        prefs = CellRpg.fetch().getConfiguration().getPreferences();
        ID = prefs.getString(Config.USER_ID);
        configLoaded = true;
    }

    public static final String getId(){
        if (!configLoaded){
            loadFromConfig();
        }
        if (ID == null){  // generate new ID if not found
            ID = UUID.randomUUID().toString();
            // and save it for later
            prefs.putString(Config.USER_ID, ID);
            prefs.flush();
        }
        return ID;
    }
}
