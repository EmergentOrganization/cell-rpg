package com.emergentorganization.cellrpg.input;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.emergentorganization.cellrpg.components.Input;

/**
 * Created by orelb on 10/29/2015.
 */
public abstract class InputProcessor {

    protected World world;

    protected ComponentMapper<Input> im;

    public InputProcessor(World world, ComponentMapper<Input> im) {
        this.world = world;
        this.im = im;
    }

    public abstract void process(int entityId);


}
