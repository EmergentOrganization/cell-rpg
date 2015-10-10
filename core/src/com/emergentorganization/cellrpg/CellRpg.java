package com.emergentorganization.cellrpg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.emergentorganization.cellrpg.scenes.mainmenu.MainMenu;
import com.emergentorganization.cellrpg.tools.Config;
import com.kotcrab.vis.ui.VisUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by BrianErikson on 6/7/2015.
 */
public class CellRpg extends Game {
    public static final String VERSION = "0.3.13";
    private static final String ATLAS_PATH = "textures/TexturePack.atlas";

    // private FPSLogger fps = new FPSLogger();
    private static CellRpg singleton;
    private final Logger logger;

    private AssetManager assetManager;
    private TextureAtlas textureAtlas;
    private Config config;

    private Screen oldScreen;

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
        config.initialize();
        if (config.isDevModeEnabled()) {
            logger.info("Disabling development mode");
            config.setDevMode(false);
        }
        else
            logger.info("Development mode disabled");

        logger.info("Loading Assets...");
        assetManager = new AssetManager(new InternalFileHandleResolver());
        assetManager.load(ATLAS_PATH, TextureAtlas.class);
        assetManager.finishLoading();
        textureAtlas = assetManager.get(ATLAS_PATH, TextureAtlas.class);

        logger.info("Loading VisUI...");
        VisUI.load();

        setScreen(new MainMenu("ready to connect"));
    }

    @Override
    public void render() {
        super.render();

        // fps.log();
    }

    @Override
    public void setScreen(Screen screen) {
        oldScreen = getScreen();
        if (oldScreen != null) {
            oldScreen.hide();
        }
        super.setScreen(screen);
    }

    @Override
    public void dispose() {
        super.dispose();

        assetManager.dispose();
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
