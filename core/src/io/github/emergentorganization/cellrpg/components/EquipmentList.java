package io.github.emergentorganization.cellrpg.components;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import io.github.emergentorganization.cellrpg.components.Weapon.Powerup;
import io.github.emergentorganization.cellrpg.core.components.Bounds;
import io.github.emergentorganization.cellrpg.core.components.Position;
import io.github.emergentorganization.cellrpg.core.entityfactory.Entities.Equipment.Equipment;
import io.github.emergentorganization.cellrpg.tools.saves.LoadoutSave;

import java.util.ArrayList;


public class EquipmentList extends Component {
    public final ArrayList<Equipment> equipment = new ArrayList<Equipment>();

    public void addEquipment(Equipment newEquip, World world, Vector2 pos, int parentId) {
        newEquip.create(world, pos, parentId);
        equipment.add(newEquip);
    }

    public void rechargeEquipment() {
        for (Equipment eq : equipment) {
            eq.recharge();
        }
    }

    public void moveEquipment(ComponentMapper<Bounds> boundsMapper, ComponentMapper<Position> posMapper) {
        for (Equipment eq : equipment) {
            eq.updatePosition(boundsMapper, posMapper);
        }
    }

    public void powerUp(Powerup powerType) {
        for (Equipment eq : equipment) {
            eq.powerUp(powerType);
        }
    }

    public void saveEquipment(){
        LoadoutSave.saveLoadout(equipment);
    }

    public boolean loadEquipment(World world, Vector2 pos, int parentId){
        // loads equipment from file
        // returns true if loaded, false if not

        ArrayList<Equipment> newEquipment = LoadoutSave.getLoadout();
        if (newEquipment != null && newEquipment.size() > 0) {
            //        for (Equipment eq : equipment){
            //            eq.dispose();
            //        }
            equipment.clear();
            for (Equipment eq : newEquipment){
                eq.create(world, pos, parentId);
                equipment.add(eq);
            }
            return true;
        } else {
            return false;
        }
    }
}
