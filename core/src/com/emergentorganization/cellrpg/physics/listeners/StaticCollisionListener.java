package com.emergentorganization.cellrpg.physics.listeners;

import com.emergentorganization.cellrpg.physics.CellUserData;
import com.emergentorganization.cellrpg.physics.Tag;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.CollisionAdapter;

/**
 * Created by BrianErikson on 9/13/15.
 */
public class StaticCollisionListener extends CollisionAdapter {

    @Override
    public boolean collision(Body body1, Body body2) {
        CellUserData d1 = (CellUserData) body1.getUserData();
        CellUserData d2 = (CellUserData) body2.getUserData();
        return !(d1.tag == Tag.STATIC && d2.tag == Tag.STATIC);
    }
}
