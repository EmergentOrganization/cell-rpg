package io.github.emergentorganization.cellrpg.components;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.core.entityfactory.Entities.Equipment.Equipment;
import io.github.emergentorganization.emergent2dcore.components.Bounds;
import io.github.emergentorganization.emergent2dcore.components.Position;

import java.util.ArrayList;


public class EquipmentList extends Component {
    public ArrayList<Equipment> equipment = new ArrayList<Equipment>();

    public void addEquipment(Equipment newEquip, World world, Vector2 pos){
        newEquip.create(world, pos);
        equipment.add(newEquip);
    }

    // TODO: this would better fit as a method of the Equipment class
    public void moveEquipment(int parentEntityId, ComponentMapper<Bounds> boundsMapper, ComponentMapper<Position> posMapper){
        equipment.get(0).updatePosition(parentEntityId, boundsMapper, posMapper);
    }
}
