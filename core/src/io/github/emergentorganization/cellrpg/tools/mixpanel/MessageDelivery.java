package io.github.emergentorganization.cellrpg.tools.mixpanel;

import com.mixpanel.mixpanelapi.MixpanelAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;

/**
 */
public class MessageDelivery implements Runnable {
    private static final Logger logger = LogManager.getLogger(MessageDelivery.class);
    private final JSONObject message;
    private final MixpanelAPI mixpanelAPI = new MixpanelAPI();

    public MessageDelivery(JSONObject message) {
        this.message = message;
    }

    @Override
    public void run() {
        try {
            mixpanelAPI.sendMessage(message);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
