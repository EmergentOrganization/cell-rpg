package com.emergentorganization.cellrpg.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.emergentorganization.cellrpg.core.EntityFactory;
import com.emergentorganization.cellrpg.managers.BodyManager;

/**
 * The bounds component represents the size of the entity in the world.
 * The values in this component are scaled down.
 *
 * Created by orelb on 10/30/2015.
 */
public class Bounds extends Component {
    public float width, height;

    public void setFromRegion(TextureRegion region) {
        System.out.println("region width: " + region.getRegionWidth());
        this.width = EntityFactory.SCALE_WORLD_TO_BOX * region.getRegionWidth();
        this.height = EntityFactory.SCALE_WORLD_TO_BOX * region.getRegionHeight();
    }
}
