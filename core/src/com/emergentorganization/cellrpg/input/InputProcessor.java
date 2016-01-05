package com.emergentorganization.cellrpg.input;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.emergentorganization.cellrpg.components.InputComponent;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

/**
 * defines interface for inputProcessors (eg player controller or scripted input controllers)
 *
 * Created by orelb on 10/29/2015.
 */
public abstract class InputProcessor {

    protected World world;
    protected ComponentMapper<InputComponent> im;

    public InputProcessor(World world, ComponentMapper<InputComponent> im) {
        this.world = world;
        this.im = im;
    }

    public abstract void process(int entityId);
    // process input on given entity
}
