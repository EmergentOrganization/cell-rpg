package com.emergentorganization.cellrpg.tools.map;

import java.util.ArrayList;

/**
 * Created by BrianErikson on 6/12/2015.
 */
public class MapLayer {
    public final LayerType type;
    private ArrayList<MapObject> objects = new ArrayList<MapObject>();

    public MapLayer(LayerType type) {this.type = type;}

    public void addMapObject(MapObject object) {
        objects.add(object);
    }

    public ArrayList<MapObject> getObjects() {
        return objects;
    }
}
