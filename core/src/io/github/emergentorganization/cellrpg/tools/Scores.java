package io.github.emergentorganization.cellrpg.tools;

import com.badlogic.gdx.Gdx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 */
public class Scores {
    static Logger logger = LogManager.getLogger(Scores.class);

    static JSONParser parser = new JSONParser();
    static Object obj;
    static JSONObject jsonObject;
    static JSONArray scoreArray;

    private static final String key = "scores";
    private static final String key_score = "score";
    private static final String key_name = "name";

    public Scores(){
        initialize();
    }

    public static void initialize(){
        try {
            obj = parser.parse(new FileReader(Gdx.files.getLocalStoragePath()
                    + File.separator + FileStructure.RESOURCE_DIR + "scores.json"));
            jsonObject = new JSONObject(obj.toString());
            scoreArray = jsonObject.getJSONArray(key);

        } catch (IOException ex){
            logger.error("cannot open scores.json file: " + ex.getMessage());
        } catch (ParseException ex){
            logger.error("malformed scores.json: " + ex.getMessage());
        } catch (JSONException ex){
            logger.error("json err reading scores: " + ex.getMessage());
        }
    }

    public int getScore(int rank){
        // returns score of given rank
        try {
            return Integer.parseInt(scoreArray.getJSONObject(rank).get(key_score).toString());
        }catch(JSONException ex){
            logger.error("score parse error @scores["+rank+"]");
            return 0;
        }
    }

    public String getName(int rank){
        // returns score of given rank
        try {
            return (String) scoreArray.getJSONObject(rank).get(key_name);
        }catch(JSONException ex){
            logger.error("score parse error @scores["+rank+"]");
            return "";
        }
    }
}
