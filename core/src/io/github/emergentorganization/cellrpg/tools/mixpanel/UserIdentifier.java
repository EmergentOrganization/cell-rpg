package io.github.emergentorganization.cellrpg.tools.mixpanel;

import com.badlogic.gdx.Preferences;
import io.github.emergentorganization.cellrpg.tools.GameSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

/**
 */
public class UserIdentifier {
    private static String ID = null;
    private static boolean configLoaded = false;
    static Logger logger = LogManager.getLogger(UserIdentifier.class);


    private static void loadFromConfig(){
        Preferences prefs = GameSettings.getPreferences();
        ID = prefs.getString(GameSettings.USER_ID);
        configLoaded = true;
    }
//
    public static final String getId(){
        if (!configLoaded){
            loadFromConfig();
        }
        if (ID == null || ID == ""){  // generate new ID if not found
            ID = UUID.randomUUID().toString();
            // and save it for later
            Preferences prefs = GameSettings.getPreferences();
            prefs.putString(GameSettings.USER_ID, ID);
            prefs.flush();
        }
        logger.info("UID:"+ID);
        return ID;
    }
}
