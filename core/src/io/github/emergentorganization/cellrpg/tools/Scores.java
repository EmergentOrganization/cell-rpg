package io.github.emergentorganization.cellrpg.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import io.github.emergentorganization.cellrpg.tools.mixpanel.UserIdentifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 */
public class Scores {
    static Logger logger = LogManager.getLogger(Scores.class);

    String saveFileName;
    JSONParser parser = new JSONParser();
    Object obj;
    JSONObject jsonObject;
    JSONArray scoreArray;
    boolean loaded = false;

    private static final String key_score_toplevel = "scores";
    private static final String key_score = "score";
    private static final String key_name = "name";
    private static final int MAX_SCORES = 9999;  // max number of scores to save

    public Scores(){
        initialize();
    }

    public void initialize(){
        saveFileName = Gdx.files.getLocalStoragePath()
                + File.separator + FileStructure.RESOURCE_DIR + "scores.json";
        loadScores();
    }

    public boolean addScore(final int score){
        // adds score if large enough. returns true if added, false if your score is pathetic.
        if (checkScoresLoaded()) {
            if (scoreArray.length() < MAX_SCORES) {
                _addScore(score);
                return true;
            } else if (score > getScore(scoreArray.length() - 1)) {
                _popScore();
                _addScore(score);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public int getScore(int rank){
        // returns score of given rank
        if (checkScoresLoaded()) {
            try {
                return Integer.parseInt(scoreArray.getJSONObject(rank).get(key_score).toString());
            } catch (JSONException ex) {
                logger.trace("score index out of range scores[" + rank + "]");
                return 0;
            }
        } else {
            return 0;
        }
    }

    public String getName(int rank){
        // returns score of given rank
        if (checkScoresLoaded()) {
            try {
                return (String) scoreArray.getJSONObject(rank).get(key_name);
            } catch (JSONException ex) {
                logger.error("score parse error @scores[" + rank + "]");
                return "";
            }
        } else {
            return "scoresReadError";
        }
    }

    public void dispose(){
        saveScores();
    }

    private boolean checkScoresLoaded(){
        // returns true if scores loaded, else prints error and attempts reload.
        if (loaded){
            return true;
        } else {
            logger.error("Cannot complete action. Scores not properly loaded. attempting reload.");
            loadScores();
            return false;
        }
    }

    private void loadScores(){
        try {
            obj = parser.parse(new FileReader(saveFileName));
            jsonObject = new JSONObject(obj.toString());
            scoreArray = jsonObject.getJSONArray(key_score_toplevel);
            loaded = true;
        } catch (IOException ex){
            logger.error("cannot open scores.json file: " + ex.getMessage());
            loaded = false;
        } catch (ParseException ex){
            logger.error("malformed scores.json: " + ex.getMessage());
            loaded = false;
        } catch (JSONException ex){
            logger.error("json err reading scores: " + ex.getMessage());
            loaded = false;
        }
    }

    private JSONObject getScoreObject(final int score){
        Preferences prefs = GameSettings.getPreferences();
        try {
            return new JSONObject()
                    .put(key_score, score)
                    .put(key_name, prefs.getString(GameSettings.KEY_USER_NAME, "NO-NAME"))
                    ;
        } catch(JSONException ex){
            logger.error("scoreObjCreateERR : ", ex);
            return null;  // TODO: is this okay? perhaps should return empty new JSONObject()?
        }
    }

    private void _addScore(final int score){
        // adds score to jsonArray, updates jsonObject. Does not check # of scores.
        int rank = scoreArray.length();
        for (int i = scoreArray.length()-1; i > -1; i--){
            if (getScore(i) < score){
                rank = i;
            }
        }
        try {
            // push down all scores after new score
            for (int i = scoreArray.length(); i > rank; i--){
                scoreArray.put(i, scoreArray.get(i-1));
            }
            // insert new score
            scoreArray.put(rank, getScoreObject(score));
            jsonObject = new JSONObject().put(key_score_toplevel, scoreArray);
        } catch (JSONException ex){
            logger.error("addScore JSONObj update ERR", ex);
        }
    }

    private void _popScore(){
        // removes and returns lowest score
        // TODO
    }

    private void saveScores(){
        if (checkScoresLoaded()) {
            try {
                FileWriter file = new FileWriter(saveFileName);
                logger.trace("saving scores:" + jsonObject);
                file.write(jsonObject.toString());
                logger.info("saved scores to file");
                file.close();
            } catch (IOException ex) {
                logger.error("failed score save", ex);
            }
        }
    }
}
