package com.emergentorganization.cellrpg.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.messages.BaseComponentMessage;

/**
 * Created by BrianErikson on 6/2/2015.
 */
public abstract class BaseComponent {
    protected ComponentType type; // Never assigned because base class cannot be constructed

    /**
     * Used to update various actions that need to be taken each frame on the component, but before rendering occurs
     * @param deltaTime The time it took to render the last frame. Usually uses Gdx.graphics.getDeltaTime();
     */
    public void update(float deltaTime) {}

    /**
     *
     * @param batch the current scene's sprite batch
     * @param pos World position of the entity
     * @param rot world rotation of the entity in degrees
     * @param scale World scale of the entity
     */
    public void render(SpriteBatch batch, Vector2 pos, float rot, Vector2 scale) {}

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
