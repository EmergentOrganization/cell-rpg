package com.emergentorganization.cellrpg.components;

import com.artemis.Component;
import com.emergentorganization.cellrpg.core.EntityFactory;
import com.emergentorganization.cellrpg.managers.BodyManager;

/**
 * The bounds component represents the size of the entity in the world.
 * The values in this component are scaled down. Use #set(width, height)
 *
 * Created by orelb on 10/30/2015.
 */
public class Bounds extends Component {

    public float width, height;

    public void set(float width, float height)
    {
        this.width = EntityFactory.SCALE_WORLD_TO_BOX * width;
        this.height = EntityFactory.SCALE_WORLD_TO_BOX * height;
    }

}
