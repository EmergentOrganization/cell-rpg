package io.github.emergentorganization.cellrpg.input.player.WeaponControls;

import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import io.github.emergentorganization.cellrpg.components.EquipmentList;
import io.github.emergentorganization.cellrpg.core.EntityID;
import io.github.emergentorganization.cellrpg.core.components.Bounds;
import io.github.emergentorganization.cellrpg.core.components.Position;
import io.github.emergentorganization.cellrpg.core.entityfactory.Entities.Equipment.Equipment;
import io.github.emergentorganization.cellrpg.core.entityfactory.Entities.Equipment.EquipmentType;
import io.github.emergentorganization.cellrpg.core.entityfactory.Entities.Equipment.Weapon;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.events.EntityEvent;
import io.github.emergentorganization.cellrpg.events.GameEvent;
import io.github.emergentorganization.cellrpg.managers.EventManager;

/**
 * static class for weapon helper methods
 */
public class WeaponUtil {

    public static void shootTo(Vector2 pos, Entity shooter, EventManager eventMan, EntityFactory entFact) {
        EquipmentList eqList = shooter.getComponent(EquipmentList.class);
        for (Equipment weaponEquip : eqList.equipment){
            if (weaponEquip.type == EquipmentType.WEAPON){
                _shootTo(pos, shooter, eventMan, entFact, (Weapon) weaponEquip);
            }
        }
    }

    public static void _shootTo(Vector2 pos, Entity shooter, EventManager eventMan, EntityFactory entFact, Weapon weapon){
        if (weapon == null) {
            eventMan.pushEvent(new EntityEvent(EntityEvent.NO_ENTITY, GameEvent.PLAYER_WEAPON_EMPTY));
            return;
        }

        if (weapon.lastShot == 0L)
            weapon.lastShot = TimeUtils.millis();

        // check if we can shoot right now
        if (TimeUtils.timeSinceMillis(weapon.lastShot) >= weapon.delay) {
            if (weapon.charge - weapon.SHOT_CHARGE_COST >= 0) {
                weapon.charge -= weapon.SHOT_CHARGE_COST;
                weapon.charge_changed = true;

                weapon.lastShot += weapon.delay;

                // get player position
                Bounds bounds = shooter.getComponent(Bounds.class);
                Vector2 center = shooter.getComponent(Position.class).getCenter(bounds, 0);
                Vector2 arm = new Vector2(0, Math.max(bounds.width, bounds.height) + .1f);

                // calculate the velocity
//                vel.set(x, y).sub(pos).nor().scl(speed);

                Vector2 dir = pos.sub(center).nor();
                arm.setAngle(dir.angle());

                entFact.createEntityByID(EntityID.BULLET, center.add(arm), dir.angle());

                eventMan.pushEvent(new EntityEvent(EntityEvent.NO_ENTITY, GameEvent.PLAYER_SHOOT));
            } // else not enough charge to shoot
            else {
                eventMan.pushEvent(new EntityEvent(EntityEvent.NO_ENTITY, GameEvent.PLAYER_WEAPON_EMPTY));
            }
        } // else trying to shoot too quickly
    }
}
