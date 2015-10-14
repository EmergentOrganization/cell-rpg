package com.emergentorganization.cellrpg.tools.mixpanel;

import com.emergentorganization.cellrpg.CellRpg;
import com.mixpanel.mixpanelapi.ClientDelivery;
import com.mixpanel.mixpanelapi.MessageBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
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

            JSONObject update = messageBuilder.set(UserIdentifier.getId(), props);

            // Send the update to mixpanel
            executor.submit(new MessageDelivery(update));
        }catch(JSONException ex){
            logger.error("analytics JSON err: " + ex.getMessage());
        }
    }

    public void newGameEvent(){
        defaultEvent("newgame");
    }

    public void startupEvent(){
        updateUserProfile();  // NOTE: only _need_ to do this if it has changed
        defaultEvent("startup");
    }

    private void defaultEvent(final String EVENT_ID) {
        try {
            JSONObject props = new JSONObject();
            // add props
            Calendar now = Calendar.getInstance();
            props.put("local time", now.get(Calendar.HOUR_OF_DAY));

            // set up the event
            JSONObject sentEvent = messageBuilder.event(UserIdentifier.getId(), EVENT_ID, props);

            // Use an instance of MixpanelAPI to send the messages
            // to Mixpanel's servers.
            executor.submit(new MessageDelivery(sentEvent));

            logger.trace("sent appStart:" + sentEvent);
        } catch (JSONException ex) {
            logger.error("analytics JSON err: " + ex.getMessage());
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
