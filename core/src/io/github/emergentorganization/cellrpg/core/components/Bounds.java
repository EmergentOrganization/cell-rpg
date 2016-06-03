package io.github.emergentorganization.cellrpg.core.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;

/**
 * The bounds component represents the size of the entity in the world.
 * The values in this component are scaled down.
 */
public class Bounds extends Component {
    public float width, height;

    public void setFromRegion(TextureRegion region) {
        this.width = EntityFactory.SCALE_WORLD_TO_BOX * region.getRegionWidth();
        this.height = EntityFactory.SCALE_WORLD_TO_BOX * region.getRegionHeight();
    }
}
