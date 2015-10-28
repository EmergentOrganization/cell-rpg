package com.emergentorganization.cellrpg.tools.mixpanel;
import com.badlogic.gdx.Preferences;
import com.emergentorganization.cellrpg.tools.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

/**
 * Created by 7yl4r on 10/14/2015.
 */
public class UserIdentifier {
    private static String ID = null;
    private static boolean configLoaded = false;
    static Logger logger = LogManager.getLogger(UserIdentifier.class);


    private static void loadFromConfig(){
        Preferences prefs = CellRpg.fetch().getConfiguration().getPreferences();
        ID = prefs.getString(Config.USER_ID);
        configLoaded = true;
    }

    public static final String getId(){
        if (!configLoaded){
            loadFromConfig();
        }
        if (ID == null || ID == ""){  // generate new ID if not found
            ID = UUID.randomUUID().toString();
            // and save it for later
            Preferences prefs = CellRpg.fetch().getConfiguration().getPreferences();
            prefs.putString(Config.USER_ID, ID);
            prefs.flush();
        }
        logger.info("UID:"+ID);
        return ID;
    }
}
