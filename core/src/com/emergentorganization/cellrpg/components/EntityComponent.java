package com.emergentorganization.cellrpg.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.emergentorganization.cellrpg.components.entity.ComponentType;
import com.emergentorganization.cellrpg.entities.Entity;

import java.util.ArrayList;

/**
 * Created by BrianErikson on 6/2/2015.
 */
public abstract class EntityComponent implements BaseComponent{
    private Entity entity; // Parent entity reference

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

    public void setEntity(Entity entity){ this.entity = entity; }

    protected Entity getEntity(){
        if (entity == null) throw new NullPointerException("Cannot get entity before component is added to parent");
        return entity;
    }

    protected  ArrayList<EntityComponent> getSiblings() {
        return getEntity().getComponents();
    }


    protected <T extends EntityComponent> ArrayList<EntityComponent> getSiblingsByType(Class<T> componentType){
        return getEntity().getComponentsByType(componentType);
    }

    protected <T extends EntityComponent> T getFirstSiblingByType(Class<T> componentType){
        return getEntity().getFirstComponentByType(componentType);
    }

  /**  protected ArrayList<EntityComponent> getSiblingsByType(ComponentType type) {
        return getEntity().getComponentsByType(type);
    }

    protected EntityComponent getFirstSiblingByType(ComponentType type) {
        return getEntity().getFirstComponentByType(type);
    }*/

    protected void addEntityToScene(Entity e) {
        getEntity().getScene().addEntity(e);
    }

    protected void removeEntityFromScene(Entity e) { getEntity().getScene().removeEntity(e); }

    public void dispose() {
    }
}