package io.github.emergentorganization.cellrpg.tools.mixpanel;

import com.badlogic.gdx.Preferences;
import io.github.emergentorganization.cellrpg.tools.saves.GameSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

/**
 */
class UserIdentifier {
    private static final Logger logger = LogManager.getLogger(UserIdentifier.class);
    private static String ID = null;
    private static boolean configLoaded = false;

    private static void loadFromConfig() {
        Preferences prefs = GameSettings.getPreferences();
        ID = prefs.getString(GameSettings.KEY_USER_ID);
        configLoaded = true;
    }

    //
    public static String getId() {
        if (!configLoaded) {
            loadFromConfig();
        }
        if (ID == null || ID.equals("")) {  // generate new ID if not found
            ID = UUID.randomUUID().toString();
            // and save it for later
            Preferences prefs = GameSettings.getPreferences();
            prefs.putString(GameSettings.KEY_USER_ID, ID);
            prefs.flush();
        }
        logger.info("UID:" + ID);
        return ID;
    }
}
