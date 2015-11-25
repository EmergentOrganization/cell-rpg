package com.emergentorganization.cellrpg.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.emergentorganization.cellrpg.core.entityfactory.EntityFactory;

/**
 * The bounds component represents the size of the entity in the world.
 * The values in this component are scaled down.
 *
 * Created by orelb on 10/30/2015.
 */
public class Bounds extends Component {
    public float width, height;

    public void setFromRegion(TextureRegion region) {
        this.width = EntityFactory.SCALE_WORLD_TO_BOX * region.getRegionWidth();
        this.height = EntityFactory.SCALE_WORLD_TO_BOX * region.getRegionHeight();
    }
}
