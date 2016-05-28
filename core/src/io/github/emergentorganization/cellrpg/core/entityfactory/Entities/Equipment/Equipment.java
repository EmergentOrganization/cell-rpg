package io.github.emergentorganization.cellrpg.core.entityfactory.Entities.Equipment;

import com.artemis.Entity;

/**
 * Abstract class which describes an equipment entity.
 */
public class Equipment {
    public String name = "Pulse Laser";
    public String description = "Basic pulse laser.";
    public EquipmentType type = EquipmentType.WEAPON;

    public boolean powered = true;
    public boolean damaged = false;

    public int baseEnergy = 1;
    public int energySlots = 4;
    public int powerFilled = 3;

    public int attackStat = 10;
    public int sheildStat = 0;
    public int moveStat   = 0;
    public int satStat    = 0;

    public void create(){
        // TODO: instantiate the equipment (inherit from EntityCreator?)
    }

    public void equip(Entity target){
        // TODO: equip this to the given target entity
    }

}
