package com.emergentorganization.cellrpg;

import com.badlogic.gdx.Game;
import com.emergentorganization.cellrpg.scenes.mainmenu.MainMenu;
import com.emergentorganization.cellrpg.tools.Config;
import com.kotcrab.vis.ui.VisUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by BrianErikson on 6/7/2015.
 */
public class CellRpg extends Game {
    public static final String VERSION = "0.2.6";

    // private FPSLogger fps = new FPSLogger();
    private static CellRpg singleton;
    private final Logger logger;
    private Config config;

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
        if (!config.isDevModeEnabled()) {
            logger.info("Enabling development mode");
            config.setDevMode(true);
        }
        else
            logger.info("Development mode enabled");

        logger.info("Loading VisUI");
        VisUI.load();

        setScreen(new MainMenu("ready to connect"));
    }

    @Override
    public void render() {
        super.render();

        // fps.log();
    }

    public Config getConfiguration() {
        return config;
    }
}
