package com.emergentorganization.cellrpg.components;

import com.emergentorganization.cellrpg.components.listeners.BaseComponentListener;
import com.emergentorganization.cellrpg.components.messages.BaseComponentMessage;
import org.dyn4j.dynamics.Body;

/**
 * Created by BrianErikson on 6/6/2015.
 */
public class PhysicsComponent extends BaseComponent {
    private Body body;

    public PhysicsComponent(Body body) {
        this.body = body;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
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
    }
}
