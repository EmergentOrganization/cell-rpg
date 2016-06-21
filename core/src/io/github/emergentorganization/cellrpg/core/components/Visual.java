package io.github.emergentorganization.cellrpg.core.components;

import com.artemis.Component;
import io.github.emergentorganization.cellrpg.components.Charge;
import io.github.emergentorganization.cellrpg.core.RenderIndex;

/**
 * This is used for both animation and static images.
 */
public class Visual extends Component {

    enum AnimationType{
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

    public void update(float deltaTime, Charge charge){
        // updates the visual
        switch (animationType) {
            case CHARGE:
                // TODO:
//                setTexture(charge.getChargeFrame(chargeComponent));
                // ...
                break;
            default:  // also case DEFAULT:
                if (isAnimation) {
                    stateTime += deltaTime;
                }
        }
    }
}
