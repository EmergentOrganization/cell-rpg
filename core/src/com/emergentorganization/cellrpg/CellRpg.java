package com.emergentorganization.cellrpg;

import com.badlogic.gdx.Game;
import com.emergentorganization.cellrpg.scenes.mainmenu.MainMenu;
import com.kotcrab.vis.ui.VisUI;

/**
 * Created by BrianErikson on 6/7/2015.
 */
public class CellRpg extends Game {
    // private FPSLogger fps = new FPSLogger();
    private static CellRpg singleton;

    public CellRpg() {
        singleton = this;
    }

    public static CellRpg fetch() {
        return singleton;
    }

    @Override
    public void create() {
        VisUI.load();

        setScreen(new MainMenu());
    }

    @Override
    public void render() {
        super.render();

        // fps.log();
    }
}
