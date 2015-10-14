package com.emergentorganization.cellrpg.tools;

import com.emergentorganization.cellrpg.CellRpg;
import com.mixpanel.mixpanelapi.ClientDelivery;
import com.mixpanel.mixpanelapi.MessageBuilder;
import com.mixpanel.mixpanelapi.MixpanelAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by 7yl4r on 10/14/2015.
 */
public class Mixpanel {
    static Logger logger = LogManager.getLogger(Mixpanel.class);

    static MessageBuilder messageBuilder;
    static MixpanelAPI mixpanel = new MixpanelAPI();

    public static void initialize(){
        messageBuilder = new MessageBuilder(Secrets.MIXPANEL_TOKEN);
    }

    public static void updateUserProfile(){
        // This creates a profile for user if one does not already exist or updates it.
        try {
            JSONObject props = new JSONObject();
            props.put("version", CellRpg.VERSION);
            props.put("OS", System.getProperty("os.name") + "v" + System.getProperty("os.version"));
            props.put("JRE", System.getProperty("java.version"));
            props.put("time zone", Calendar.getInstance().getTimeZone().getID());

            JSONObject update = messageBuilder.set(UserIdentifier.getId(), props);

            // Send the update to mixpanel
            try {
                mixpanel.sendMessage(update);
            } catch (IOException ex) {
            logger.error("message deliver error:" + ex.getMessage());
            }
        }catch(JSONException ex){
            logger.error("analytics JSON err: " + ex.getMessage());
        }
    }

    public static void newGameEvent(){
        defaultEvent("newgame");
    }

    public static void startupEvent(){
        updateUserProfile();  // NOTE: only _need_ to do this if it has changed
        defaultEvent("startup");
    }

    private static void defaultEvent(final String EVENT_ID) {
        Thread messageThread = new Thread() {
            public void run() {
                try {
                    JSONObject props = new JSONObject();
                    // add props
                    Calendar now = Calendar.getInstance();
                    props.put("local time", now.get(Calendar.HOUR_OF_DAY));

                    // set up the event
                    JSONObject sentEvent = messageBuilder.event(UserIdentifier.getId(), EVENT_ID, props);

                    // Gather together a bunch of messages into a single
                    // ClientDelivery. This can happen in a separate thread
                    // or process from the call to MessageBuilder.event()
                    ClientDelivery delivery = new ClientDelivery();
                    delivery.addMessage(sentEvent);

                    // Use an instance of MixpanelAPI to send the messages
                    // to Mixpanel's servers.
                    try {
                        mixpanel.deliver(delivery);
                    } catch (IOException ex) {
                        logger.error("message deliver error:" + ex.getMessage());
                    }
                    logger.trace("sent appStart:" + sentEvent);
                } catch (JSONException ex) {
                    logger.error("analytics JSON err: " + ex.getMessage());
                }
            }
        };
        messageThread.start();
    }
}
