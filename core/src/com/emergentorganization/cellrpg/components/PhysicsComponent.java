package com.emergentorganization.cellrpg.components;

import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.listeners.BaseComponentListener;
import com.emergentorganization.cellrpg.components.messages.BaseComponentMessage;
import com.emergentorganization.cellrpg.physics.Tag;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;

/**
 * Created by BrianErikson on 6/6/2015.
 */
public class PhysicsComponent extends BaseComponent {
    private final World world;
    private final Body body;
    private final MovementComponent moveComponent;

    public PhysicsComponent(World world, Body body, Tag tag) {
        this.world = world;
        this.body = body;
        world.addBody(body);
        moveComponent = (MovementComponent) getFirstSiblingByType(ComponentType.MOVEMENT);
        body.setUserData(new CellUserData(moveComponent, tag));
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        Vector2 pos = moveComponent.getWorldPosition();
        body.getTransform().setTranslation(pos.x, pos.y); // TODO: Center collider on sprite
    }

    @Override
    public ComponentType getType() {
        return super.getType();
    }

    @Override
    public void addListener(BaseComponentListener listener) {
        super.addListener(listener);
    }

    @Override
    public void removeListener(BaseComponentListener listener) {
        super.removeListener(listener);
    }

    @Override
    public void receiveMessage(BaseComponentMessage message) {
        super.receiveMessage(message);
    }

    @Override
    protected void broadcast(BaseComponentMessage message) {
        super.broadcast(message);
    }

    @Override
    protected void broadcast(ComponentType type, BaseComponentMessage message) {
        super.broadcast(type, message);
    }

    @Override
    public void dispose() {
        super.dispose();
        world.removeBody(body);
    }

    public class CellUserData {
        public final Tag tag;
        public final MovementComponent movementComponent;

        public CellUserData(MovementComponent movementComponent, Tag tag) {
            this.movementComponent = movementComponent;
            this.tag = tag;
        }
    }
}
