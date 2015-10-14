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

    public static void initialize(){
        messageBuilder = new MessageBuilder(Secrets.MIXPANEL_TOKEN);
    }

    public static void startupEvent(){
        final String EVENT_ID = "startup";
        // You can send properties along with events
        JSONObject props = new JSONObject();
        try{
            // add props
            props.put("version", CellRpg.VERSION);
            props.put("OS", System.getProperty("os.name") + "v" + System.getProperty("os.version"));
            props.put("JRE", System.getProperty("java.version"));
            Calendar now = Calendar.getInstance();
            props.put("time zone", now.getTimeZone().getID());
            props.put("local time", now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE));

            // set up the event
            JSONObject sentEvent = messageBuilder.event(UserIdentifier.getId(), EVENT_ID, props);

            // Gather together a bunch of messages into a single
            // ClientDelivery. This can happen in a separate thread
            // or process from the call to MessageBuilder.event()
            ClientDelivery delivery = new ClientDelivery();
            delivery.addMessage(sentEvent);

            // Use an instance of MixpanelAPI to send the messages
            // to Mixpanel's servers.
            MixpanelAPI mixpanel = new MixpanelAPI();
            try {
                mixpanel.deliver(delivery);
            } catch (IOException ex) {
                logger.error("message deliver error:" + ex.getMessage());
            }
            logger.info("sent appStart:" + sentEvent);
        }

        catch(JSONException ex){
            logger.error("analytics JSON err: " + ex.getMessage());
        }
    }

}
