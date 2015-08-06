package com.emergentorganization.cellrpg.scenes;

import com.emergentorganization.cellrpg.entities.CAGrid;
import com.emergentorganization.cellrpg.entities.FollowingCamera;
import com.emergentorganization.cellrpg.entities.ZIndex;
import com.emergentorganization.cellrpg.entities.characters.Player;
import com.emergentorganization.cellrpg.scenes.listeners.EntityActionListener;

/**
 * Pausable scene with cellular automata grid functionality.
 * Created by Tylar on 2015-07-14.
 */
public class CAScene extends PausableScene {
    private CAGrid ca_grid;
    private final int RENDERS_PER_GLIDER_INSERT = 10;  // TODO: temporary for testing only!
    private int render_n = 0;  // TODO: temporary for testing only!

    @Override
    public void create() {
        super.create();

        addEntityListener(new EntityActionListener(Player.class) {
            private FollowingCamera followingCamera;

            @Override
            public void onAdd() {
                ca_grid = new CAGrid(3, ZIndex.CHARACTER);  // TODO: what is the proper ZIndex?
                addEntity(ca_grid);
            }

            @Override
            public void onRemove() {

                removeEntity(ca_grid);
            }
        });
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        // TODO: this next part temporary for testing only:
        if (render_n > RENDERS_PER_GLIDER_INSERT){
            render_n = 0;
            int[][] testPattern = {
                    {0,1,0},
                    {0,0,1},
                    {1,1,1}
            };
            int x = (int)(Math.random()*(ca_grid.getSizeX() - testPattern.length));
            int y = (int)(Math.random()*(ca_grid.getSizeY() - testPattern[0].length));
            ca_grid.stampState(testPattern, x, y);
            //System.out.println("inserting glider @ " + x + "," + y);
        } else {
            render_n++;
        }
    }

}
