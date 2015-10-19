package com.emergentorganization.cellrpg.scenes;

import com.emergentorganization.cellrpg.entities.ca.CAGridBase;
import com.emergentorganization.cellrpg.entities.characters.Player;
import com.emergentorganization.cellrpg.scenes.listeners.EntityActionListener;
import com.emergentorganization.cellrpg.scenes.regions.Region;

import java.util.Random;

/**
 * Pausable scene with cellular automata grid functionality.
 * Created by Tylar on 2015-07-14.
 */
public abstract class CAScene extends PausableScene {
    private Region currentRegion;
    public static Random randomGenerator = new Random();

    @Override
    public void create() {
        super.create();

        currentRegion = getStartingRegion();
        currentRegion.addCALayers();

        addEntityListener(new EntityActionListener(Player.class) {

            @Override
            public void onAdd() {
            }

            @Override
            public void onRemove() {
                currentRegion.removeCALayers();
            }
        });
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }

    public CAGridBase getLayer(CALayer layer){
        return currentRegion.getLayer(layer);
    }

    public abstract Region getStartingRegion();
}
