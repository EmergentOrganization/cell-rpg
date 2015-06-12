package com.emergentorganization.cellrpg.tools.map;

import com.emergentorganization.cellrpg.entities.backgrounds.TheEdge;
import com.emergentorganization.cellrpg.entities.buildings.BuildingLarge1;
import com.emergentorganization.cellrpg.entities.buildings.BuildingRound1;
import com.emergentorganization.cellrpg.entities.characters.npcs.CharCiv1Blinker;
import com.emergentorganization.cellrpg.scenes.Scene;

import java.util.ArrayList;

/**
 * Created by BrianErikson on 6/12/2015.
 */
public class Map {
    private Scene scene;
    private ArrayList<MapLayer> layers = new ArrayList<MapLayer>();

    public ArrayList<MapLayer> getLayers() {
        return layers;
    }

    public void addLayer(MapLayer layer) {
        layers.add(layer);
    }

    public void load(Scene targetScene) {
        scene = targetScene;
        for (MapLayer layer : layers) {
            loadLayer(layer);
        }
    }

    private void loadLayer(MapLayer layer) {
        for (MapObject object : layer.getObjects()) {
            if (object instanceof MapImage) {
                loadImage((MapImage)object);
            }
            else {
                throw new RuntimeException("ERROR: loading for object type" + object.getClass().getName() + " is unsupported");
            }
        }
    }

    private void loadImage(MapImage image) {
        print("Loading " + image.type.name() + " at pos:" + image.getPosition().toString());
        switch (image.type) {
            case THE_EDGE:
                scene.addEntity(new TheEdge(image.getTexture(), image.getPosition()));
                break;
            case BUILDING_LARGE_1:
                scene.addEntity(new BuildingLarge1(image.getTexture(), image.getPosition()));
                break;
            case BUILDING_ROUND_1:
                scene.addEntity(new BuildingRound1(image.getTexture(), image.getPosition()));
                break;
            case CHAR_CIV1_BLINKER:
                scene.addEntity(new CharCiv1Blinker(image.getTexture(), image.getPosition()));
                break;
        }
    }

    private static void print(String str) {
        System.out.println("[Map] " + str);
    }
}