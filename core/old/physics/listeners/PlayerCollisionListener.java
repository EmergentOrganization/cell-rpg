package com.emergentorganization.cellrpg.physics.listeners;

import com.emergentorganization.cellrpg.components.entity.MovementComponent;
import com.emergentorganization.cellrpg.physics.PlayerUserData;
import com.emergentorganization.cellrpg.physics.Tag;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;

/**
 * Created by BrianErikson on 6/8/2015.
 */
public class PlayerCollisionListener extends CharacterCollisionListener {
    public PlayerCollisionListener() {
        super(Tag.PLAYER);
    }

    @Override
    public boolean collision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2, Penetration penetration) {
        boolean ignored = super.collision(body1, fixture1, body2, fixture2, penetration);
        if (!ignored) { // player must've been hit
            if (body1.getUserData() instanceof PlayerUserData) {
                PlayerUserData data1 = (PlayerUserData) body1.getUserData();
                updateDestination(data1);
                return false;
            }
            else if (body2.getUserData() instanceof PlayerUserData) {
                PlayerUserData data2 = (PlayerUserData) body2.getUserData();
                updateDestination(data2);
                return false;
            }
        }
        return true;
    }

    private void updateDestination(PlayerUserData data) {
        if (data.entity.getFirstComponentByType(MovementComponent.class).getMoveState() == MovementComponent.MoveState.PATH_FOLLOW) {
            data.playerInputComponent.skipDest();
        }
    }
}
