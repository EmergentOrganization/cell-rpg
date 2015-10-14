package com.emergentorganization.cellrpg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.emergentorganization.cellrpg.scenes.mainmenu.MainMenu;
import com.emergentorganization.cellrpg.tools.Config;
import com.kotcrab.vis.ui.VisUI;
import com.mixpanel.mixpanelapi.ClientDelivery;
import com.mixpanel.mixpanelapi.MessageBuilder;
import com.mixpanel.mixpanelapi.MixpanelAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;

/**
 * Created by BrianErikson on 6/7/2015.
 */
public class CellRpg extends Game {
    public static final String VERSION = loadVersion();
    private static final String ATLAS_PATH = "textures/TexturePack.atlas";

    // private FPSLogger fps = new FPSLogger();
    private static CellRpg singleton;
    private final Logger logger;

    private AssetManager assetManager;
    private TextureAtlas textureAtlas;
    private Config config;

    private Screen curScreen;

    public CellRpg() {
        singleton = this;
        System.setProperty("log4j.configurationFile", "log4j2.xml");
        logger = LogManager.getLogger(getClass());
        config = new Config();
    }

    public static CellRpg fetch() {
        return singleton;
    }

    @Override
    public void create() {
        // MIXPANEL TEST
        // load secret token
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(Gdx.files.getLocalStoragePath() + "secrets.json"));

            org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) obj;

            final String PROJECT_TOKEN = (String) jsonObject.get("mixpanel_token");
            final String USER_ID = "alpha_testing_user";

            MessageBuilder messageBuilder =
                    new MessageBuilder(PROJECT_TOKEN);

            // You can send properties along with events
            JSONObject props = new JSONObject();
            try {
                // add props
                props.put("version", VERSION);
                props.put("OS", System.getProperty ("os.name") + "v" + System.getProperty ("os.version"));
                props.put("JRE", System.getProperty ("java.version"));
                Calendar now = Calendar.getInstance();
                props.put("time zone", now.getTimeZone().getID());
                props.put("local time", now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE));

                // set up the event
                JSONObject sentEvent =
                        messageBuilder.event(USER_ID, "app start", props);

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
                    logger.error("message deliver erorr:" + ex.getMessage());
                }
                logger.info("sent appStart:" + sentEvent);
            } catch (JSONException ex) {
                logger.error("analytics JSON err: " + ex.getMessage());
            }
        } catch (IOException ex){
            logger.error("cannot open secrets.json file: " + ex.getMessage());
        } catch (ParseException ex){
            logger.error("malformed secrets.json: " + ex.getMessage());
        }
        // END MIXPANEL TEST


        config.initialize();
        if (!config.isDevModeEnabled()) {
            logger.info("Enabling development mode");
            config.setDevMode(true);
        }
        else
            logger.info("Development mode enabled");

        logger.info("Loading Assets...");
        assetManager = new AssetManager(new InternalFileHandleResolver());
        assetManager.load(ATLAS_PATH, TextureAtlas.class);
        assetManager.finishLoading();
        textureAtlas = assetManager.get(ATLAS_PATH, TextureAtlas.class);

        logger.info("Loading VisUI...");
        VisUI.load();

        super.setScreen(new MainMenu("ready to connect"));
        curScreen = getScreen();
    }

    @Override
    public void render() {
        super.render();

        if (curScreen != screen) {
            screen.dispose();
            super.setScreen(curScreen);
        }
        // fps.log();
    }

    @Override
    public void setScreen(Screen screen) {
        //super.setScreen(screen);  // super method called in render for proper disposal
        curScreen = screen;
    }

    @Override
    public void dispose() {
        super.dispose();

        assetManager.dispose();
    }

    public static String loadVersion() {
        Properties props = new Properties();
        File propsFile = new File("property.settings");
        try {
            FileReader reader = new FileReader(propsFile);
            props.load(reader);
            String major = props.getProperty("majorVersion");
            String minor = props.getProperty("minorVersion");
            String revision = props.getProperty("revision");
            reader.close();
            return major + "." + minor + "." + revision;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Config getConfiguration() {
        return config;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public TextureAtlas getTextureAtlas() {
        return textureAtlas;
    }
}
