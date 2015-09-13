package com.emergentorganization.cellrpg;

import com.badlogic.gdx.Game;
import com.emergentorganization.cellrpg.scenes.RPGScene;
import com.emergentorganization.cellrpg.scenes.mainmenu.MainMenu;
import com.kotcrab.vis.ui.VisUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by BrianErikson on 6/7/2015.
 */
public class CellRpg extends Game {
    public static final String VERSION = "0.2.2";

    // private FPSLogger fps = new FPSLogger();
    private static CellRpg singleton;
    private final Logger logger;

    public CellRpg() {
        singleton = this;
        System.setProperty("log4j.configurationFile", "log4j2.xml");
        logger = LogManager.getLogger(getClass());
    }

    public static CellRpg fetch() {
        return singleton;
    }

    @Override
    public void create() {
        logger.info("Loading VisUI");
        VisUI.load();

        setScreen(new MainMenu("ready to connect"));
    }

    @Override
    public void render() {
        super.render();

        // fps.log();
    }
}
