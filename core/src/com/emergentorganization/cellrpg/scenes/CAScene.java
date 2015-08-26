package com.emergentorganization.cellrpg.scenes;

import com.badlogic.gdx.graphics.Color;
import com.emergentorganization.cellrpg.entities.CAGrid;
import com.emergentorganization.cellrpg.entities.FollowingCamera;
import com.emergentorganization.cellrpg.entities.ZIndex;
import com.emergentorganization.cellrpg.entities.characters.Player;
import com.emergentorganization.cellrpg.scenes.listeners.EntityActionListener;

import java.util.EnumMap;
import java.util.Map;

/**
 * Pausable scene with cellular automata grid functionality.
 * Created by Tylar on 2015-07-14.
 */
public class CAScene extends PausableScene {
    private Map<CALayer, CAGrid> ca_layers = new EnumMap<CALayer, CAGrid>(CALayer.class);
    private final int RENDERS_PER_GLIDER_INSERT = 10;  // TODO: temporary for testing only!
    private int render_n = 0;  // TODO: temporary for testing only!

    @Override
    public void create() {
        super.create();

        addEntityListener(new EntityActionListener(Player.class) {
            private FollowingCamera followingCamera;

            @Override
            public void onAdd() {
                ca_layers.put(CALayer.VYROIDS, new CAGrid(11, ZIndex.VYROIDS, new Color[] {new Color(0f, .8f, .5f, .4f)}));
                ca_layers.put(CALayer.ENERGY,  new CAGrid(11, ZIndex.VYROIDS, new Color[] {new Color(0f, .1f, .8f, .2f)}));
                addEntity(ca_layers.get(CALayer.VYROIDS));
                addEntity(ca_layers.get(CALayer.ENERGY));
            }

            @Override
            public void onRemove() {
                removeEntity(ca_layers.get(CALayer.VYROIDS));
                removeEntity(ca_layers.get(CALayer.ENERGY));
            }
        });
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        // TODO: this next part temporary for testing only:
        if (render_n > RENDERS_PER_GLIDER_INSERT){
            //addRandomSpawn();
        } else {
            render_n++;
        }
    }

    private void addRandomSpawn(){
        CAGrid vyroidLayer = ca_layers.get(CALayer.VYROIDS);
        render_n = 0;
        int[][] testPattern = {
                {0,1,0},
                {0,0,1},
                {1,1,1}
        };
        int x = (int)(Math.random()*(vyroidLayer.getSizeX() - testPattern.length));
        int y = (int)(Math.random()*(vyroidLayer.getSizeY() - testPattern[0].length));
        vyroidLayer.stampState(testPattern, x, y);
        //System.out.println("inserting glider @ " + x + "," + y);
    }

    public CAGrid getLayer(CALayer layer){
        return ca_layers.get(layer);
    }
}
