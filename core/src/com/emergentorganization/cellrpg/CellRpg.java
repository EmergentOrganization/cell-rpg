package com.emergentorganization.cellrpg;

import com.badlogic.gdx.Game;
import com.emergentorganization.cellrpg.scenes.Test;

/**
 * Created by BrianErikson on 6/7/2015.
 */
public class CellRpg extends Game {
    @Override
    public void create() {
        setScreen(new Test());
    }
}
