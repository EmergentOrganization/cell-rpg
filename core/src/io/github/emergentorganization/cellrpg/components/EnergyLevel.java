package io.github.emergentorganization.cellrpg.components;

import com.artemis.Component;
import com.badlogic.gdx.Preferences;
import io.github.emergentorganization.cellrpg.tools.saves.GameStateSave;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class EnergyLevel extends Component {
    private final Logger logger = LogManager.getLogger(getClass());
    // energyLevel represents the constant energy flow available
    // levels should not be constantly generated/replenished, instead they are
    // near-constant in value. The total amount of energy should only change
    // when the player's configuration or environment changes.
    // Allocate/free energy methods are used when assigning energy units to equipment.
    private int availableEnergy = 5;
    private int allocatedEnergy = 0;

    public boolean allocateEnergy(int amount) {
        // allocates given amount of energy if available, returns true. Else returns false.
        if (hasEnoughEnergyFor(amount)) {
            availableEnergy -= amount;
            allocatedEnergy += amount;
            return true;
        } else {
            return false;
        }
    }

    public boolean freeEnergy(int amount) {
        // frees up given amount of energy, returns true. Returns false if attempting to free unallocated energy.
        if (allocatedEnergy >= amount) {
            allocatedEnergy -= amount;
            availableEnergy += amount;
            return true;
        } else {
            logger.info("Cannot free " + amount + "energy. Only " + allocatedEnergy + " allocated.");
            return false;
        }
    }

    private boolean hasEnoughEnergyFor(int requiredEnergy) {
        // return true if can accommodate given energy requirements
        return requiredEnergy <= availableEnergy;
    }

    public int energyAvailable() {  // getEnergy
        return availableEnergy;
    }

    public void addEnergy(int delta) {  // setEnergy
        if (delta > 0) {
            this.availableEnergy += delta;
            onEnergyChange();
        } else {
            logger.error("cannot add negative energy");
        }
    }

    private void onEnergyChange() {
        // TODO: fire energy level changed event
    }

    public void save(){
        Preferences gameSave = GameStateSave.getPreferences();
        gameSave.putInteger(GameStateSave.KEY_AVAILABLE_ENERGY_LEVEL, availableEnergy);
        gameSave.putInteger(GameStateSave.KEY_ALLOCATED_ENERGY_LEVEL, allocatedEnergy);
        gameSave.flush();
    }

    public boolean load(){
        Preferences gameSave = GameStateSave.getPreferences();
        availableEnergy = gameSave.getInteger(GameStateSave.KEY_AVAILABLE_ENERGY_LEVEL, 5);
        allocatedEnergy = gameSave.getInteger(GameStateSave.KEY_ALLOCATED_ENERGY_LEVEL, 0);
        return true;
    }
}
