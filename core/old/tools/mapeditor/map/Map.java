package com.emergentorganization.cellrpg.tools.mapeditor.map;

import com.emergentorganization.cellrpg.entities.Entity;

import java.util.ArrayList;

/**
 * Created by BrianErikson on 6/19/2015.
 */
public class Map {
    private ArrayList<Entity> entities = new ArrayList<Entity>();

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void setEntities(ArrayList<Entity> entities) {
        this.entities = entities;
    }
}
