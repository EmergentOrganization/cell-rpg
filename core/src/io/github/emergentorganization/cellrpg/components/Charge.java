package io.github.emergentorganization.cellrpg.components;

import com.artemis.Component;
import io.github.emergentorganization.cellrpg.core.components.Visual;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// charge represents an amount of energy in this entity ready to be used.
// It's only use at time of writing is for use on Equipment entities.
// Note the important distinction between the Charge component (amount constantly fluctuating for use in equipment)
// and the EnergyLevel component (relatively stable energy-balance data for entities with equipment).


public class Charge extends Component {
    private int charge = 0;  // how much charge stored in entity
    // equipment energy charge stored for use
    public int recharge_per_s = 1;
    public int maxCharge = charge;  // max charge achievable

    // charge getter/setter
    public void set(int newCharge){
        charge = newCharge;
        checkCharge();
    }
    public int get(){
        return charge;
    }
    public int addCharge(int deltaCharge){
        // adds given charge and then checks value.
        // returns 0 if charge is fine, -1 if charge was too low, +1 if charge is too high.
        charge += deltaCharge;
        return checkCharge();
    }

    public int checkCharge(){
        // checks and ensures charge value is too high or low.
        // returns 0 if charge is fine, -1 if charge was too low, +1 if charge is too high.
        if (charge < 0){
            charge = 0;
            return -1;
        } else if (charge > maxCharge){
            charge = maxCharge;
            return 1;
        } else {
            return 0;
        }
    }

    public void recharge(int scale) {
        // recharges 1 second worth of charge multiplied by given scale (powerLevel)
        if (charge < maxCharge) {
            addCharge(recharge_per_s * scale);
        }
    }

    public float getChargeFrameTime(float maxFrame){
        // returns index of animation frame which matches current charge level scaled to maxframe
        float frameTime = (float)charge / (float)maxCharge * maxFrame;
        logger.trace("charge " + charge + "/" + maxCharge + " = frame " + frameTime + "/" + maxFrame);
        return frameTime;
    }
    private final Logger logger = LogManager.getLogger(getClass());
}
