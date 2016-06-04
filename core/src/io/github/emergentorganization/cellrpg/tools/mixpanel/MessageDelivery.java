package io.github.emergentorganization.cellrpg.tools.mixpanel;

import com.mixpanel.mixpanelapi.MixpanelAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A Runnable class designed to send a MixPanel message in a non-blocking fashion through an ExecutorService.<br>
 * <br>
 * NOTE: If you wish to use the message once the class has been instantiated, please lock it first using Synchronized<br>
 */
public class MessageDelivery implements Runnable {
    private static final Logger logger = LogManager.getLogger(MessageDelivery.class);
    private final JSONObject message;

    public MessageDelivery(JSONObject message) {
        this.message = message;
    }

    @Override
    public void run() {
        try {
            // Lock the message reference in case the main thread still holds a reference to the message
            synchronized (message) {
                new MixpanelAPI().sendMessage(message);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
