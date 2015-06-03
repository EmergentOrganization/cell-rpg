package com.emergentorganization.cellrpg.entities;

/**
 * Created by BrianErikson on 6/2/2015.
 */

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.emergentorganization.cellrpg.components.BaseComponent;
import com.emergentorganization.cellrpg.components.ComponentType;
import com.emergentorganization.cellrpg.components.MovementComponent;
import com.emergentorganization.cellrpg.components.messages.BaseComponentMessage;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * The root class for every object in the game
 */
public abstract class Entity {
    private ArrayList<BaseComponent> components = new ArrayList<BaseComponent>();

    /**
     * The movement component is out of the array because movement is implied for each entity.
     * Performance degradation is only incurred when the entity is moved, rotated, or scaled
     */
    private MovementComponent moveComponent = new MovementComponent();

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
                component.render(batch, moveComponent.getWorldPosition(),
                                 moveComponent.getRotation(), moveComponent.getScale());
            }
        }
    }

    /**
     * Returns the first component in the iterator. Used for performance reasons when only one component is expected
     * @param type the type of component
     * @return derived component, or null if there is none
     */
    public BaseComponent getFirstComponentByType(ComponentType type) {
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

    public void broadcastMessage(BaseComponentMessage message) {
        // TODO
    }

    public void broadcastMessage(ComponentType type, BaseComponentMessage message) {
        // TODO
    }

    public MovementComponent getMovementComponent() {
        return moveComponent;
    }

    public void dispose() {
        moveComponent.dispose();
        for (BaseComponent component : components) {
            component.dispose();
        }
    }
}
