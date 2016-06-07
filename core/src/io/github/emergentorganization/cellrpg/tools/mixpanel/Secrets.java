package io.github.emergentorganization.cellrpg.tools.mixpanel;

import com.badlogic.gdx.Gdx;
import io.github.emergentorganization.cellrpg.tools.FileStructure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

/**
 */
public class Secrets {
    private static Logger logger = LogManager.getLogger(Secrets.class);

    private static JSONParser parser = new JSONParser();

    public static String MIXPANEL_TOKEN;

    public static void initialize(){
        try {
            Object obj = parser.parse(new FileReader(Gdx.files.getLocalStoragePath()
                    + FileStructure.RESOURCE_DIR + "secrets.json"));
            JSONObject jsonObject = (org.json.simple.JSONObject) obj;

            MIXPANEL_TOKEN = (String) jsonObject.get("mixpanel_token");

        } catch (IOException ex){
            logger.error("cannot open secrets.json file: " + ex.getMessage());
        } catch (ParseException ex){
            logger.error("malformed secrets.json: " + ex.getMessage());
        }
    }
}
