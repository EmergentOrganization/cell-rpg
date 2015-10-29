package com.emergentorganization.cellrpg.input;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.math.Vector3;
import com.emergentorganization.cellrpg.components.Position;

/**
 * Created by orelb on 10/29/2015.
 */
public abstract class InputProcessor {

    protected World world;

    protected ComponentMapper<Position> pm;

    public InputProcessor(World world){
        this.world = world;
    }

    public abstract void process(int entityId);


}
