package io.github.emergentorganization.cellrpg.core.entityfactory.Entities.Equipment;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.emergent2dcore.components.Bounds;
import io.github.emergentorganization.emergent2dcore.components.Position;

/**
 * Abstract class which describes an equipment entity.
 */
public abstract class Equipment {
    // TODO: remove/replace defaults
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

    public abstract void create(World world, Vector2 pos);
        // instantiates the equipment (inherit from EntityCreator?)

    public abstract void updatePosition(int parentEntityId, ComponentMapper<Bounds> boundsMapper, ComponentMapper<Position> posMapper);
}
