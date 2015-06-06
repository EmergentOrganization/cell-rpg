package com.emergentorganization.cellrpg.physics;

import com.emergentorganization.cellrpg.components.MovementComponent;
import org.dyn4j.dynamics.Body;

/**
 * Created by BrianErikson on 6/6/2015.
 */
public class CellBody extends Body {
    private final MovementComponent movementComponent;

    public CellBody(MovementComponent component) {
        super();

        movementComponent = component;
    }

    public MovementComponent getMovementComponent() {
        return movementComponent;
    }
}
