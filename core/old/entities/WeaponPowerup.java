package com.emergentorganization.cellrpg.entities;

import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.entity.*;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.entities.EntityEvents;
import com.emergentorganization.cellrpg.entities.ZIndex;
import com.emergentorganization.cellrpg.scenes.CALayer;
import com.emergentorganization.cellrpg.scenes.arcadeScore;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 7yl4r on 2015-09-27.
 */
public class WeaponPowerup extends Powerup {
    private Timer timer;

    public WeaponPowerup(){
        super("game/powerup-star");
    }

    protected void applyPowerupCollection(){
        // temporary big increase
        getScene().getPlayer().getFirstComponentByType(WeaponComponent.class).increaseRechargeRate(10);

        // then scale back to just a small increase
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getScene().getPlayer().getFirstComponentByType(WeaponComponent.class).decreaseRechargeRate(9);
            }
        }, 7*1000);  // 7s
    }

    @Override
    public void dispose() {
        if (timer != null)
            timer.cancel();

        super.dispose();
    }
}

