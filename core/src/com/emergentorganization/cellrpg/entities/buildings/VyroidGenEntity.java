package com.emergentorganization.cellrpg.entities.buildings;

import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.entity.MovementComponent;
import com.emergentorganization.cellrpg.entities.Entity;
import com.emergentorganization.cellrpg.entities.ZIndex;
import com.emergentorganization.cellrpg.entities.ca.CAGridBase;
import com.emergentorganization.cellrpg.scenes.CALayer;
import com.emergentorganization.cellrpg.scenes.CAScene;

/**
 * sub-entity which includes random warp-in (generator) of vyroids seeded into the area around its location.
 * Extend this rather than entity if you want your building to spawn vyroids randomly in the area around it.
 * No graphics or physics included with this "invisible building"
 * Created by 7yl4r on 2015-09-27.
 */
public class VyroidGenEntity extends Entity {
    int genSinceLastSpawn = 0;
    protected int GEN_BTWN_SPAWNS = 1;  // generations between spawn drop-ins
    protected int[][] pattern = {  // pre-pulsar
            {0,1,0,0,0,0,0,1,0},
            {1,1,1,0,0,0,1,1,1}
    };
    protected float MAX_SPAWN_DIST = 500;  // max distance away from building to spawn

    /*
    This constructor is needed for MapEditor. Do not remove.
     */
    public VyroidGenEntity(ZIndex zindex) {
        super(zindex);
    }

    public VyroidGenEntity(ZIndex zindex, Vector2 position) {
        super(zindex);
        getFirstComponentByType(MovementComponent.class).setWorldPosition(position);
    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);

        if (genSinceLastSpawn > GEN_BTWN_SPAWNS) {
            // add randomly generated spawn
            try {
                CAScene scen = (CAScene) getScene();
                MovementComponent mc = getFirstComponentByType(MovementComponent.class);
                float x = mc.getWorldPosition().x + (float) (Math.random() - .5) * MAX_SPAWN_DIST;
                float y = mc.getWorldPosition().y + (float) (Math.random() - .5) * MAX_SPAWN_DIST;
                CAGridBase layr = scen.getLayer(CALayer.VYROIDS);
                layr.stampState(pattern, x, y);
            } catch(ClassCastException err){
                // scene is not CSScene
                return;
            }
            genSinceLastSpawn = 0;
        } else {
            genSinceLastSpawn += 1;
        }

    }
}
