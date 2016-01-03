package com.emergentorganization.cellrpg.input;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.emergentorganization.cellrpg.components.InputComponent;

/**
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


}
