package io.github.emergentorganization.cellrpg.core.entityfactory.Entities.Equipment;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.components.Weapon.Powerup;
import io.github.emergentorganization.emergent2dcore.components.Bounds;
import io.github.emergentorganization.emergent2dcore.components.Position;

/**
 * Abstract class which describes an equipment entity.
 */
public abstract class Equipment {
    // TODO: remove/replace defaults
    public String name = "unnamed";
    public String description = "description not given.";
    public EquipmentType type = EquipmentType.CONTROLLER;
    public int parentId = -1;

    public boolean damaged = false;

    public int baseEnergy  = 0;
    public int energySlots = 0;
    public int powerFilled = 0;

    public int attackStat = 0;
    public int shieldStat = 0;
    public int moveStat   = 0;
    public int satStat    = 0;

    public Equipment(int parentId, String name, String description, int baseEnergy, int energySlots){
        this.parentId = parentId;
        this.name = name;
        this.description = description;

        this.baseEnergy = baseEnergy;
        this.energySlots = energySlots;
    }

    public boolean isPowered(){
        return powerFilled > baseEnergy;  // must be greater else just enough power to run but not to apply anything
    }

    public abstract void create(World world, Vector2 pos);  // TODO: pass parentEntityId here
    // instantiates the equipment Entity as child of parentEntityId

    public void recharge(int FREQ) {
        // energy management functions for the equipment. Called by EnergySystem.

        // TODO: take some charge from the energySystem and give it to the equipment
    }

    public void powerUp(Powerup type){
        // powers up the equipment (if applicable) with given powerup
    }

    public abstract void updatePosition(ComponentMapper<Bounds> boundsMapper, ComponentMapper<Position> posMapper);
    // movement management functions for the equipment. Acts to keep equipment entity next to parent.
    // Called by MovementSystem.
}
