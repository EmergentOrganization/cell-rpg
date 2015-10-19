package com.emergentorganization.cellrpg.tools.mixpanel;

import com.emergentorganization.cellrpg.CellRpg;
import com.emergentorganization.cellrpg.components.entity.input.InputComponent;
import com.emergentorganization.cellrpg.components.entity.input.PlayerInputComponent;
import com.emergentorganization.cellrpg.scenes.CALayer;
import com.emergentorganization.cellrpg.scenes.CAScene;
import com.emergentorganization.cellrpg.scenes.Scene;
import com.emergentorganization.cellrpg.scenes.arcadeScore;
import com.mixpanel.mixpanelapi.ClientDelivery;
import com.mixpanel.mixpanelapi.MessageBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by 7yl4r on 10/14/2015.
 */
public class Mixpanel {
    static Logger logger = LogManager.getLogger(Mixpanel.class);

    private MessageBuilder messageBuilder;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public void initialize(){
        messageBuilder = new MessageBuilder(Secrets.MIXPANEL_TOKEN);
    }

    public void updateUserProfile(){
        // This creates a profile for user if one does not already exist or updates it.
        try {
            JSONObject props = new JSONObject();
            props.put("version", CellRpg.VERSION);
            props.put("OS", System.getProperty("os.name") + "v" + System.getProperty("os.version"));
            props.put("JRE", System.getProperty("java.version"));
            props.put("time zone", Calendar.getInstance().getTimeZone().getID());
            // TODO: (maybe) useful mixpanel special properties:
            //props.put("$email", "???");
            //props.put("$ip", "???");
            //props.put("$first_name", "???");
            //props.put("$last_name", "???");
            //props.put("$name", "???"); // alternative to $first_name $last_name
            //props.put("$created", "???");
            //props.put("$email", "???");
            //props.put("$phone", "???");

            JSONObject update = messageBuilder.set(UserIdentifier.getId(), props);
            // Send the update to mixpanel
            executor.submit(new MessageDelivery(update));
        }catch(JSONException ex){
            logger.error("analytics JSON err: " + ex.getMessage());
        }
    }

    public void newGameEvent(){
        incrementProperty("games_played", 1);
    }

    public void startupEvent(){
        updateUserProfile();  // NOTE: only _need_ to do this if it has changed
        try {
            Calendar now = Calendar.getInstance();
            JSONObject props = new JSONObject();
            props.put("local time", now.get(Calendar.HOUR_OF_DAY));
            defaultEvent("startup", props);
        } catch (JSONException ex) {
            logger.error("analytics JSON err: " + ex.getMessage());
        }
    }

    public void gameOverEvent(final Scene scene) {
        try{
            JSONObject props = new JSONObject();

            // report score:
            int score = -1;  // score = -1 for non-scoring scenes
            if (scene instanceof arcadeScore){
                score = ((arcadeScore)scene).getScore();
            }
            props.put("score", score);

            // report input method @ game end
            props.put("input_method",
                scene.getPlayer().getFirstComponentByType(PlayerInputComponent.class).getCurrentInputMethod().getName()
            );

            // report CA generation lengths
            try {
                if (scene instanceof CAScene) {
                    props.put("min_gen_time",
                            ((CAScene) scene).getLayer(CALayer.VYROIDS_MEGA).minGenTime  // mega should be fastest (least cells)
                    );
                    props.put("max_gen_time",
                            ((CAScene) scene).getLayer(CALayer.VYROIDS_MINI).maxGenTime  // mini should be slowest (most cells)
                    );
                }
            } catch (NullPointerException ex){  // scene doesn't have requested CALayer
                // nvm it
            }

            defaultEvent("game_over", props);
        } catch (JSONException ex) {
            logger.error("analytics JSON err: " + ex.getMessage());
        }

    }

    private void defaultEvent(final String EVENT_ID, JSONObject props ) {
        // basic single event with given properties
        // set up the event
        JSONObject sentEvent = messageBuilder.event(UserIdentifier.getId(), EVENT_ID, props);

        // Use an instance of MixpanelAPI to send the messages
        // to Mixpanel's servers.
        executor.submit(new MessageDelivery(sentEvent));

        logger.trace("sent appStart:" + sentEvent);
    }

    private void addToList(final String LIST_ID, final String ADDITION){
        // adds given addition to given list.
        // Useful for keeping a list of "achievements", "places visited", etc.
        try {
            JSONObject properties = new JSONObject();
            properties.put(LIST_ID, ADDITION);
            JSONObject update = messageBuilder.append(UserIdentifier.getId(), properties);
            // Send the update to mixpanel
            executor.submit(new MessageDelivery(update));
        } catch (JSONException ex) {
            logger.error("addToList analytics JSON err: " + ex.getMessage());
        }
    }

    private void incrementProperty(final String PROPERTY_ID, final long INCREMENT){
        // basic change to incremental event with single property
        // Useful for keeping a running count of games played, powerups collected, etc.
        Map<String, Long> properties = new HashMap<String, Long>();  // Pass a Map to increment multiple properties
        properties.put(PROPERTY_ID, INCREMENT);
        JSONObject update = messageBuilder.increment(UserIdentifier.getId(), properties);
        // Send the update to mixpanel
        executor.submit(new MessageDelivery(update));
    }

    // for use w/ trackCharge()
    public enum chargeType{
        DONATION,
        CHARACTER_COSMETIC
    }

    private void trackCharge(final long AMOUNT, final chargeType TYPE){
        // Track a charge of AMOUNT with given TYPE
        // Useful for managing revenue (maybe one day...)
        try{
            JSONObject properties = new JSONObject();
            properties.put("type", TYPE.toString());
            JSONObject update =
                    messageBuilder.trackCharge(UserIdentifier.getId(), AMOUNT, properties);
            executor.submit(new MessageDelivery(update));
        } catch (JSONException ex) {
            logger.error("trackCharge analytics JSON err: " + ex.getMessage());
        }
    }

    public void dispose() {
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
