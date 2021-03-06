package io.github.emergentorganization.cellrpg.core.components;

import com.artemis.Component;
import io.github.emergentorganization.cellrpg.components.Charge;
import io.github.emergentorganization.cellrpg.core.RenderIndex;
import io.github.emergentorganization.cellrpg.managers.AssetManager;

/**
 * This is used for both animation and static images.
 */
public class Visual extends Component {

    public enum AnimationType{
        DEFAULT, CHARGE
    }

    public String id; // the visual id, this is resolved through a manager.
    public boolean isAnimation = false;
    public RenderIndex index = RenderIndex.BACKGROUND;
    public AnimationType animationType = AnimationType.DEFAULT;

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

    public void update(float deltaTime, Charge charge, AssetManager assetManager){
        // updates the visual
        switch (animationType) {
            case CHARGE:
                stateTime = charge.getChargeFrameTime(assetManager.getAnimation(id).getAnimationDuration());
                break;
            default:  // also case DEFAULT:
                if (isAnimation) {
                    stateTime += deltaTime;
                }
        }
    }
}
