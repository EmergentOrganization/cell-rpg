package com.emergentorganization.cellrpg.tools.mixpanel;

import com.badlogic.gdx.Gdx;
import com.emergentorganization.cellrpg.tools.FileStructure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by 7yl4r on 10/14/2015.
 */
public class Secrets {
    static Logger logger = LogManager.getLogger(Secrets.class);

    static JSONParser parser = new JSONParser();
    static Object obj;
    static org.json.simple.JSONObject jsonObject;

    public static String MIXPANEL_TOKEN;

    public static void initialize(){
        try {
            obj = parser.parse(new FileReader(Gdx.files.getLocalStoragePath() + File.separator + FileStructure.RESOURCE_DIR + "secrets.json"));
            jsonObject = (org.json.simple.JSONObject) obj;

            MIXPANEL_TOKEN = (String) jsonObject.get("mixpanel_token");

        } catch (IOException ex){
            logger.error("cannot open secrets.json file: " + ex.getMessage());
        } catch (ParseException ex){
            logger.error("malformed secrets.json: " + ex.getMessage());
        }
    }
}
