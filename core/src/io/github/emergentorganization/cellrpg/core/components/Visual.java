package io.github.emergentorganization.cellrpg.core.components;

import com.artemis.Component;
import io.github.emergentorganization.cellrpg.core.RenderIndex;

/**
 * This is used for both animation and static images.
 */
public class Visual extends Component {

    public String id; // the visual id, this is resolved through a manager.
    public boolean isAnimation = false;
    public RenderIndex index = RenderIndex.BACKGROUND;

    public float stateTime; // animation timestep

    public void setAnimation(String id) {
        this.id = id;
        isAnimation = true;

        stateTime = 0; // reset animation
    }

    public void setTexture(String id) {
        this.id = id;
        isAnimation = false;
    }

}
