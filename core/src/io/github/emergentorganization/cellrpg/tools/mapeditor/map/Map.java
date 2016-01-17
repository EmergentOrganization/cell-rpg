package com.emergentorganization.cellrpg.tools.mapeditor.map;

import com.artemis.Entity;

import java.util.ArrayList;


public class Map {
    private ArrayList<Entity> entities = new ArrayList<Entity>();

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<Entity> entities) {
        this.entities = entities;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }
}
