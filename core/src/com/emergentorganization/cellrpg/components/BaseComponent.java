package com.emergentorganization.cellrpg.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.listeners.BaseComponentListener;
import com.emergentorganization.cellrpg.components.messages.BaseComponentMessage;
import com.emergentorganization.cellrpg.entities.Entity;

import java.util.ArrayList;

/**
 * Created by BrianErikson on 6/2/2015.
 */
public abstract class BaseComponent {
    protected ComponentType type; // Never assigned because base class cannot be constructed
    private Entity entity; // Parent entity reference
    private ArrayList<BaseComponentListener> listeners = new ArrayList<BaseComponentListener>();

    /**
     * Called when the component is added to an entity
     * This is useful to fetch information/components from the parent entity
     */
    public void added(){}

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

    public void setEntity(Entity entity){ this.entity = entity; }

    protected Entity getEntity(){ return entity; };

    public void addListener(BaseComponentListener listener){
        listeners.add(listener);
    }

    public void removeListener(BaseComponentListener listener){
        listeners.remove(listener);
    }

    /**
     * Used to delegate events to an entity's components. Override this method to provide functionality
     * @param message the message to handle
     */
    public void receiveMessage(BaseComponentMessage message) {
        for(BaseComponentListener listener: listeners) {
            // check if the listener is interested in this message
            if (listener.validate(message)) {
                listener.run(this, message);
            }
        }
    }

    /**
     * Used to delegate events to an entity's components. Override this method to provide functionality
     * @param message the message to handle
     */
    protected void broadcast(BaseComponentMessage message) {
        entity.broadcastMessage(message);
    }

    protected void broadcast(ComponentType type, BaseComponentMessage message) {
        entity.broadcastMessage(type, message);
    }

    protected  ArrayList<BaseComponent> getSiblings() {
        return entity.getComponents();
    }

    protected ArrayList<BaseComponent> getSiblingsByType(ComponentType type) {
        return entity.getComponentsByType(type);
    }

    protected BaseComponent getFirstSiblingByType(ComponentType type) {
        return entity.getFirstComponentByType(type);
    }

    protected void addEntityToScene(Entity e) {
        if (getEntity().getScene() == null) throw new NullPointerException("Cannot add a new entity to scene before" +
                " the parent entity is added by the scene");

        getEntity().getScene().addEntity(e);
    }

    public void dispose() {}
}