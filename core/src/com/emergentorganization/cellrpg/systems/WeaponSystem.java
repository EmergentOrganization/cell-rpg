package com.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.IntervalEntityProcessingSystem;
import com.emergentorganization.cellrpg.components.Weapon.WeaponComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * System for managing weapon behavior.
 *
 * Handles:
 *  - weapon powerups returning weapon to normal after powerup has worn off.
 *  - weapon recharging
 */
public class WeaponSystem extends IntervalEntityProcessingSystem {
    private final Logger logger = LogManager.getLogger(getClass());
    static final int FREQ = 1;

    public WeaponSystem() {
        super(Aspect.all(WeaponComponent.class), FREQ);
    }

    public void process(Entity ent) {
        WeaponComponent weaponComp = ent.getComponent(WeaponComponent.class);

        // power-down powerups if timers are up
        for (int i = weaponComp.powerup_timers.size()-1; i > -1; i--) {
        // NOTE: goes max->0 to avoid index issues when removing
            if (weaponComp.powerup_timers.get(i) < 0) {  // if timer expired
                weaponComp.powerDown(i);
            } else {
                weaponComp.powerup_timers.set(i, weaponComp.powerup_timers.get(i) - FREQ);
            }
        }

        // recharge weapon
        weaponComp.charge += weaponComp.recharge_per_s*FREQ;
    }
}
