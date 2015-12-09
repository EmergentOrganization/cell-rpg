package com.emergentorganization.cellrpg.components;

import com.artemis.Component;
import com.emergentorganization.cellrpg.core.RenderIndex;

/**
 * This is used for both animation and static images.
 * <p/>
 * Created by orelb on 10/28/2015.
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
