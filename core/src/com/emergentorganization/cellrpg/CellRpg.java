package com.emergentorganization.cellrpg;

import com.badlogic.gdx.Game;
import com.emergentorganization.cellrpg.tools.mapeditor.MapEditor;
import com.kotcrab.vis.ui.VisUI;

/**
 * Created by BrianErikson on 6/7/2015.
 */
public class CellRpg extends Game {
    // private FPSLogger fps = new FPSLogger();

    @Override
    public void create() {
        VisUI.load();

        setScreen(new MapEditor());
    }

    @Override
    public void render() {
        super.render();

        // fps.log();
    }
}
