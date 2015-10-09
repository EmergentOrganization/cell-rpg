package com.emergentorganization.cellrpg.entities;

import com.emergentorganization.cellrpg.components.entity.ShieldComponent;
import com.emergentorganization.cellrpg.components.entity.WeaponComponent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 7yl4r on 2015-09-27.
 */
public class ShieldPowerup extends Powerup {
    public ShieldPowerup(){
        super("game/powerup-plus");
    }

    protected void applyPowerupCollection(){
        // temporary big increase
        getScene().getPlayer().getFirstComponentByType(ShieldComponent.class).increaseRechargeRate(10);

        // then scale back to just a small increase
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getScene().getPlayer().getFirstComponentByType(ShieldComponent.class).decreaseRechargeRate(9);
            }
        }, 7*1000);  // 7s
    }
}

