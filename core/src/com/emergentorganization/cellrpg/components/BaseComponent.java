package com.emergentorganization.cellrpg.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.emergentorganization.cellrpg.components.messages.BaseComponentMessage;

/**
 * Created by BrianErikson on 6/2/2015.
 */
public abstract class BaseComponent {
    private ComponentType type; // Never assigned because base class cannot be constructed

    /**
     * Used to update various actions that need to be taken each frame on the component, but before rendering occurs
     * @param deltaTime The time it took to render the last frame. Usually uses Gdx.graphics.getDeltaTime();
     */
    public void update(float deltaTime) {}

    /**
     * Renders the component to the screen after the update method is called
     * @param batch the scene batch
     */
    public void render(SpriteBatch batch) {}

    /**
     * Override this method if child component should call the render method each frame
     * @return boolean: should the component render?
     */
    public boolean shouldRender() {
        return false;
    }

    public ComponentType getType() {
        return type;
    }

    /**
     * Used to delegate events to an entity's components. Override this method to provide functionality
     * @param message the message to handle
     */
    public void receiveMessage(BaseComponentMessage message) {}

    public void dispose() {}
}
