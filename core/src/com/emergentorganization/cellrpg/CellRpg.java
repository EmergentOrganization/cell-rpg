package com.emergentorganization.cellrpg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.FPSLogger;
import com.emergentorganization.cellrpg.scenes.Test;

/**
 * Created by BrianErikson on 6/7/2015.
 */
public class CellRpg extends Game {
    // private FPSLogger fps = new FPSLogger();

    @Override
    public void create() {
        setScreen(new Test());
    }

    @Override
    public void render() {
        super.render();

        // fps.log();
    }
}
