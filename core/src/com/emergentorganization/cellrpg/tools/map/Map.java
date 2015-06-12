package com.emergentorganization.cellrpg.tools.map;

import java.util.ArrayList;

/**
 * Created by BrianErikson on 6/12/2015.
 */
public class Map {
    private ArrayList<MapLayer> layers = new ArrayList<MapLayer>();

    public ArrayList<MapLayer> getLayers() {
        return layers;
    }

    public void addLayer(MapLayer layer) {
        layers.add(layer);
    }

    public void load() {
        //TODO
        print("Initialization has not yet been implemented");
    }

    private static void print(String str) {
        System.out.println("[Map] " + str);
    }
}
