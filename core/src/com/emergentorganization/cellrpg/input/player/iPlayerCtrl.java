package com.emergentorganization.cellrpg.input.player;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.emergentorganization.cellrpg.components.InputComponent;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

/**
 * Created by 7yl4r on 1/4/2016.
 */
public abstract class iPlayerCtrl {
    World world;
    ComponentMapper<InputComponent> inp_m;

    public iPlayerCtrl(World world, ComponentMapper<InputComponent> inp_m){
        this.world = world;
        this.inp_m = inp_m;
    }

    public abstract String getName();
    // return the user-facing name of the processor

    public abstract void addInputConfigButtons(VisTable table, VisWindow menuWindow);
    // adds config menu items

    public abstract void process(Entity player);
    // process input on given player entity
}
