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
    private float bulletSpeed = 200f; // Bullet speed
    private float bulletDistance = 1000f; // How far can a bullet reach (world units)
    private float shootDelay = 200f; // Time between shots

    private boolean firstBullet = true; // Used to make timing more accurate
    private long lastShot;
    private Vector2 tmp = new Vector2();

    public WeaponComponent(){
        type = ComponentType.WEAPON;
    }

    @Override
    public void added() {
        mc = (MovementComponent) getFirstSiblingByType(ComponentType.MOVEMENT);
    }

    public void shootTo(float x, float y){
        // check if we can shoot right now
        if(TimeUtils.timeSinceMillis(lastShot) >= shootDelay) {
            lastShot += shootDelay;

            // get player position
            Vector2 pos = mc.getLocalPosition();

            // set temporary vector to bullet position
            tmp.set(x, y);

            // calculate the destination
            Vector2 dest = tmp.sub(pos).nor().scl(bulletDistance);

            addEntityToScene(new Bullet(pos, dest, bulletSpeed));

            System.out.println("Shooting to " + dest);
        }
    }

}
