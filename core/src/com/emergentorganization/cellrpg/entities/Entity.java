package com.emergentorganization.cellrpg.entities;

/**
 * Created by BrianErikson on 6/2/2015.
 */

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.emergentorganization.cellrpg.components.EntityComponent;
import com.emergentorganization.cellrpg.components.entity.ComponentType;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;
import com.emergentorganization.cellrpg.scenes.Scene;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * The root class for every object in the game
 */
public abstract class Entity {
    private Scene parentScene;
    public final ZIndex zIndex;
    private ArrayList<EntityComponent> components = new ArrayList<EntityComponent>();

    /**
     * The movement component is out of the array because movement is implied for each entity.
     * Performance degradation is only incurred when the entity is moved, rotated, or scaled
     */
    private MovementComponent moveComponent = new MovementComponent(); // TODO: move it into the array

    public Entity(){
        zIndex = ZIndex.BUILDING;
        moveComponent.setEntity(this); // make sure to call super from child classes or null pointer exceptions may occur
        moveComponent.setScale(Scene.scale);
    }

    public Entity(ZIndex zIndex) {
        this.zIndex = zIndex;
        moveComponent.setEntity(this);
        moveComponent.setScale(Scene.scale);
    }

    public Entity(ZIndex zIndex, float scale) {
        this.zIndex = zIndex;
        moveComponent.setEntity(this);
        moveComponent.setScale(scale);
    }

    /**
     * This is called when the entity is added to the world.
     */
    public void added(){
    }

    /**
     * Calls update method on all added components before render method
     * @param deltaTime the time passed between each frame render
     */
    public void update(float deltaTime) {
        moveComponent.update(deltaTime);
        for (EntityComponent component : components) {
            component.update(deltaTime);
        }
    }

    /**
     * Calls render method on all added components after the update method if component.shouldRender() returns true
     * @param batch the scene batch
     */
    public void render(SpriteBatch batch) {
        for (EntityComponent component : components) {
            if (component.shouldRender()) {
                component.render(batch);
            }
        }
    }

    /**
     * Calls debug render method on all added components after the render method if component.shouldRender() returns true
     * @param renderer the scene's ShapeRenderer
     */
    public void debugRender(ShapeRenderer renderer) {
        for (EntityComponent component : components) {
            if (component.shouldDebugRender()) {
                component.debugRender(renderer);
            }
        }
    }

    public ArrayList<EntityComponent> getComponents() {
        return components;
    }

    /**
     * Returns the first component in the iterator. Used for performance reasons when only one component is expected
     * @param type the type of component
     * @return derived component, or null if there is none
    public EntityComponent getFirstComponentByType(ComponentType type) {
        if(type == ComponentType.MOVEMENT)
            return moveComponent;

        for (EntityComponent component : components) {
            if (component.getType() == type) {
                return component;
            }
        }
        return null;
    }     */


    /**
     * Returns the first component in the iterator. Used for performance reasons when only one component is expected
     * @param componentType the component class
     * @return derived component, or null if there is none
     */
    public <T extends EntityComponent> T getFirstComponentByType(Class<T> componentType){
        if(componentType == MovementComponent.class)
            return (T)moveComponent;

        for (EntityComponent component : components) {
            if (component.getClass() == componentType) {
                return (T)component;
            }
        }
        return null;
    }

    /*public ArrayList<EntityComponent> getComponentsByType(ComponentType type) {
        ArrayList<EntityComponent> comps = new ArrayList<EntityComponent>();

        for (EntityComponent component : this.components) {
            if (component.getType() == type) {
                comps.add(component);
            }
        }

        return comps;
    }  */

    public <T extends EntityComponent> ArrayList<EntityComponent> getComponentsByType(Class<T> componentType) {
        ArrayList<EntityComponent> comps = new ArrayList<EntityComponent>();

        for (EntityComponent component : this.components) {
            if (component.getClass() == componentType) {
                comps.add(component);
            }
        }

        return comps;
    }

    public void addComponent(EntityComponent component) {
        component.setEntity(this);
        component.added();
        components.add(component);
    }

    public void removeComponent(EntityComponent component) {
        components.remove(component);
    }

    /**

    public void removeAllComponentsByType(ComponentType type) {
        final ListIterator<EntityComponent> iterator = components.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next().getType() == type) {
                iterator.remove();
            }
        }
    }
     */


    public <T extends EntityComponent> void removeAllComponentsByType(Class<T> componentType) {
        final ListIterator<EntityComponent> iterator = components.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next().getClass() == componentType) {
                iterator.remove();
            }
        }
    }

    public MovementComponent getMovementComponent() {
        return moveComponent;
    }


    public Scene getScene() {
        if (parentScene == null) throw new NullPointerException("Cannot get scene before entity is added by it");

        return parentScene;
    }

    public void setScene(Scene scene) {
        parentScene = scene;
    }

    public void dispose() {
        moveComponent.dispose();
        for (EntityComponent component : components) {
            component.dispose();
        }
    }
}
