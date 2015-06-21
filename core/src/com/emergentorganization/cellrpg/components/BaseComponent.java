package com.emergentorganization.cellrpg.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
     */
    public void render(SpriteBatch batch) {}

    /**
     * Override this method if child component should call the render method each frame
     * @return boolean: should the component render?
     */
    public boolean shouldRender() {
        return false;
    }

    public void debugRender(ShapeRenderer renderer) {}

    /**
     * Override this method if child component should call the debug render method each frame
     * @return boolean: should the component render the debug?
     */
    public boolean shouldDebugRender() {return false;}

    public ComponentType getType() {
        return type;
    }

    public void setEntity(Entity entity){ this.entity = entity; }

    protected Entity getEntity(){
        if (entity == null) throw new NullPointerException("Cannot get entity before component is added to parent");
        return entity;
    }

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
        getEntity().broadcastMessage(message);
    }

    protected void broadcast(ComponentType type, BaseComponentMessage message) {
        getEntity().broadcastMessage(type, message);
    }

    protected  ArrayList<BaseComponent> getSiblings() {
        return getEntity().getComponents();
    }

    protected ArrayList<BaseComponent> getSiblingsByType(ComponentType type) {
        return getEntity().getComponentsByType(type);
    }

    protected BaseComponent getFirstSiblingByType(ComponentType type) {
        return getEntity().getFirstComponentByType(type);
    }

    protected void addEntityToScene(Entity e) {
        getEntity().getScene().addEntity(e);
    }

    protected void removeEntityFromScene(Entity e) { getEntity().getScene().removeEntity(e); }

    public void dispose() {
        listeners.clear();
    }
}