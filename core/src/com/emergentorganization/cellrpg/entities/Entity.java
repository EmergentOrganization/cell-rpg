package com.emergentorganization.cellrpg.entities;

/**
 * Created by BrianErikson on 6/2/2015.
 */

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.emergentorganization.cellrpg.components.BaseComponent;
import com.emergentorganization.cellrpg.components.ComponentType;
import com.emergentorganization.cellrpg.components.messages.BaseComponentMessage;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * The root class for every object in the game
 */
public abstract class Entity {
    ArrayList<BaseComponent> components = new ArrayList<BaseComponent>();

    /**
     * Calls update method on all added components before render method
     * @param deltaTime the time passed between each frame render
     */
    public void update(float deltaTime) {
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

    public void addComponent(BaseComponent component) {
        components.add(component);
    }

    public void removeComponent(BaseComponent component) {
        components.remove(component);
    }

    public void removeAllComponentsWithType(ComponentType type) {
        final ListIterator<BaseComponent> iterator = components.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next().getType() == type) {
                iterator.remove();
            }
        }
    }

    public void broadcastMessage(BaseComponentMessage message) {

    }

    public void broadcastMessage(ComponentType type, BaseComponentMessage message) {

    }

    public void dispose() {
        for (BaseComponent component : components) {
            component.dispose();
        }
    }
}
