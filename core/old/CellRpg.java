package com.emergentorganization.cellrpg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.emergentorganization.cellrpg.sound.BgSoundController;
import com.emergentorganization.cellrpg.tools.FileStructure;
import com.emergentorganization.cellrpg.tools.mixpanel.Mixpanel;
import com.emergentorganization.cellrpg.tools.mixpanel.Secrets;
import com.emergentorganization.cellrpg.scenes.mainmenu.MainMenu;
import com.emergentorganization.cellrpg.tools.Config;
import com.kotcrab.vis.ui.VisUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by BrianErikson on 6/7/2015.
 */
public class CellRpg extends Game {
    private String version;
    private static final String ATLAS_PATH = FileStructure.RESOURCE_DIR + "textures" + File.separator + "TexturePack.atlas";

    public static final BgSoundController bgSoundController = BgSoundController.fetch();

    // private FPSLogger fps = new FPSLogger();
    private static CellRpg singleton;
    private final Logger logger;

    private AssetManager assetManager;
    private TextureAtlas textureAtlas;
    private Config config;
    private Mixpanel mixpanel;

    private Screen curScreen;

    public CellRpg() {
        singleton = this;
        System.setProperty("log4j.configurationFile", FileStructure.RESOURCE_DIR + "log4j2.xml");
        logger = LogManager.getLogger(getClass());
        config = new Config();
        mixpanel = new Mixpanel();
    }

    public static CellRpg fetch() {
        return singleton;
    }

    @Override
    public void create() {
        FileStructure.fetch().initialize();
        version = loadVersion();
        config.initialize();  // must come before Mixpanel.init
        bgSoundController.initialize();
        Secrets.initialize();
        mixpanel.initialize();

        mixpanel.startupEvent();
        if (!config.isDevModeEnabled()) {
            logger.info("Enabling development mode");
            config.setDevMode(true);
        }
        else
            logger.info("Development mode enabled");

        logger.info("Loading Assets...");
        assetManager = new AssetManager(new InternalFileHandleResolver());
        assetManager.load(ATLAS_PATH, TextureAtlas.class);
        loadSounds();
        assetManager.finishLoading();
        textureAtlas = assetManager.get(ATLAS_PATH, TextureAtlas.class);

        logger.info("Loading VisUI...");
        VisUI.load();

        super.setScreen(new MainMenu("ready to connect"));
        curScreen = getScreen();
    }

    private void loadSounds() {
        String prefix = FileStructure.RESOURCE_DIR + "sounds" + File.separator;
        String ext = ".wav";
        String[] sounds = {
                "Hit",
                "PlayerHurt",
                "Select",
                "ShieldDown",
                "Shoot",
                "ShootBlank"
        };

        for (String sound : sounds) {
            assetManager.load(prefix + sound + ext, Sound.class);
        }

    }

    @Override
    public void render() {
        super.render();

        bgSoundController.step(Gdx.graphics.getDeltaTime());

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

        bgSoundController.dispose();
        assetManager.dispose();
        mixpanel.dispose();
    }

    public String loadVersion() {
        Properties props = new Properties();
        File propsFile = Gdx.files.internal(FileStructure.RESOURCE_DIR + "property.settings").file();
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

    public Mixpanel getMixpanel() {
        return mixpanel;
    }

    public String getVersion() {
        return version;
    }
}
