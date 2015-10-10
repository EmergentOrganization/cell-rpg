package com.emergentorganization.cellrpg.scenes;

import com.badlogic.gdx.graphics.Color;
import com.emergentorganization.cellrpg.entities.ca.BufferedCAGrid;
import com.emergentorganization.cellrpg.entities.ca.CAGridBase;
import com.emergentorganization.cellrpg.entities.FollowingCamera;
import com.emergentorganization.cellrpg.entities.ZIndex;
import com.emergentorganization.cellrpg.entities.ca.decayCAGrid;
import com.emergentorganization.cellrpg.entities.characters.Player;
import com.emergentorganization.cellrpg.scenes.listeners.EntityActionListener;

import java.util.EnumMap;
import java.util.Map;

/**
 * Pausable scene with cellular automata grid functionality.
 * Created by Tylar on 2015-07-14.
 */
public class CAScene extends PausableScene {
    private Map<CALayer, CAGridBase> ca_layers = new EnumMap<CALayer, CAGridBase>(CALayer.class);
    private final int RENDERS_PER_GLIDER_INSERT = 10;  // TODO: temporary for testing only!
    private int render_n = 0;  // TODO: temporary for testing only!

    @Override
    public void create() {
        super.create();

        addEntityListener(new EntityActionListener(Player.class) {
            private FollowingCamera followingCamera;

            @Override
            public void onAdd() {
                ca_layers.put(CALayer.VYROIDS_MINI, new BufferedCAGrid(
                        1, ZIndex.VYROIDS, new Color[]{new Color(1f, .2f, .2f, 1f), new Color(1f, .4f, .8f, .8f)}
                ));
                ca_layers.put(CALayer.VYROIDS_MEGA, new BufferedCAGrid(
                        11, ZIndex.VYROIDS, new Color[]{new Color(.2f, .2f, 1f, 1f), new Color(1f, .4f, .8f, .8f)}
                ));
                ca_layers.put(CALayer.VYROIDS, new BufferedCAGrid(
                        3, ZIndex.VYROIDS, new Color[] {new Color(1f, .87f, .42f, 1f), new Color(1f, .4f, .8f, .8f)}
                ));
                ca_layers.put(CALayer.ENERGY,  new decayCAGrid(
                        1, ZIndex.VYROIDS, new Color[] {new Color(1f, 1f, 1f, .8f)}
                ));
                addEntity(ca_layers.get(CALayer.VYROIDS_MEGA));
                addEntity(ca_layers.get(CALayer.VYROIDS_MINI));
                addEntity(ca_layers.get(CALayer.VYROIDS));
                addEntity(ca_layers.get(CALayer.ENERGY));
            }

            @Override
            public void onRemove() {
                removeEntity(ca_layers.get(CALayer.VYROIDS_MEGA));
                removeEntity(ca_layers.get(CALayer.VYROIDS_MINI));
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
        CAGridBase vyroidLayer = ca_layers.get(CALayer.VYROIDS);
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

    public CAGridBase getLayer(CALayer layer){
        return ca_layers.get(layer);
    }
}
