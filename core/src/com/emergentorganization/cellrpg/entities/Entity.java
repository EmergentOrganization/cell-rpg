package com.emergentorganization.cellrpg.entities;

/**
 * Created by BrianErikson on 6/2/2015.
 */

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.emergentorganization.cellrpg.components.BaseComponent;
import com.emergentorganization.cellrpg.components.ComponentType;
import com.emergentorganization.cellrpg.components.MovementComponent;
import com.emergentorganization.cellrpg.components.messages.BaseComponentMessage;
import com.emergentorganization.cellrpg.scenes.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * The root class for every object in the game
 */
public abstract class Entity {
    private Scene parentScene;
    public final ZIndex zIndex;
    private ArrayList<BaseComponent> components = new ArrayList<BaseComponent>();

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
        for (BaseComponent component : components) {
            component.update(deltaTime);
        }
    }

    /**
     * Calls render method on all added components after the update method if component.shouldRender() returns true
     * @param batch the scene batch
     */
    public void render(SpriteBatch batch) {
        for (BaseComponent component : components) {
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
        for (BaseComponent component : components) {
            if (component.shouldDebugRender()) {
                component.debugRender(renderer);
            }
        }
    }

    public ArrayList<BaseComponent> getComponents() {
        return components;
    }

    /**
     * Returns the first component in the iterator. Used for performance reasons when only one component is expected
     * @param type the type of component
     * @return derived component, or null if there is none
     */
    public BaseComponent getFirstComponentByType(ComponentType type) {
        if(type == ComponentType.MOVEMENT)
            return moveComponent;

        for (BaseComponent component : components) {
            if (component.getType() == type) {
                return component;
            }
        }
        return null;
    }

    public ArrayList<BaseComponent> getComponentsByType(ComponentType type) {
        ArrayList<BaseComponent> comps = new ArrayList<BaseComponent>();

        for (BaseComponent component : this.components) {
            if (component.getType() == type) {
                comps.add(component);
            }
        }

        return comps;
    }

    public void addComponent(BaseComponent component) {
        component.setEntity(this);
        component.added();
        components.add(component);
    }

    public void removeComponent(BaseComponent component) {
        components.remove(component);
    }

    public void removeAllComponentsByType(ComponentType type) {
        final ListIterator<BaseComponent> iterator = components.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next().getType() == type) {
                iterator.remove();
            }
        }
    }

    /**
     * Broadcasts a message to all of the components
     * @param message the message to be broadcasted.
     */
    public void broadcastMessage(BaseComponentMessage message) {
        moveComponent.receiveMessage(message);

        for(BaseComponent comp : components){
            comp.receiveMessage(message);
        }
    }

    /**
     * Broadcasts a message to a specific component type.
     * @param type the component type
     * @param message the message to be broadcasted.
     */
    public void broadcastMessage(ComponentType type, BaseComponentMessage message) {
        // I don't see why this is necessary, but let it be.
        if(type == ComponentType.MOVEMENT)
        {
            moveComponent.receiveMessage(message);
            return;
        }

        List<BaseComponent> comps = getComponentsByType(type);
        for(BaseComponent comp : comps)
        {
            comp.receiveMessage(message);
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
        for (BaseComponent component : components) {
            component.dispose();
        }
    }
}
