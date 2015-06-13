package com.emergentorganization.cellrpg.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.emergentorganization.cellrpg.entities.characters.Bullet;

/**
 * Created by OrelBitton on 06/06/2015.
 */
public class WeaponComponent extends BaseComponent{

    private MovementComponent mc;

    // TODO: weapon class
    private float speed = 100f;
    private float maxDist = 50f;
    private float delay = 200;

    private long lastShot;
    private Vector2 vel = new Vector2();

    public WeaponComponent(){
        type = ComponentType.WEAPON;
    }

    @Override
    public void added() {
        mc = (MovementComponent) getFirstSiblingByType(ComponentType.MOVEMENT);
    }

    public void shootTo(float x, float y){
        // check if we can shoot right now
        if(TimeUtils.timeSinceMillis(lastShot) >= delay) {
            lastShot += delay;

            // get player position
            Vector2 pos = mc.getLocalPosition();

            // calculate the velocity
            vel.set(x, y).sub(pos).nor().scl(speed);

            addEntityToScene(new Bullet(pos, vel, maxDist));
        }
    }

}
