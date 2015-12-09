package com.emergentorganization.cellrpg.systems.CARenderSystem;

import com.artemis.BaseEntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.emergentorganization.cellrpg.components.CAGridComponents;
import com.emergentorganization.cellrpg.systems.CARenderSystem.CACell.BaseCell;

/**
 * Handles CA grid state generations and initialization.
 *
 * Created by 7yl4r on 12/9/2015.
 */
public class CAGenerationSystem extends BaseEntitySystem {

    @Override
    protected  void processSystem() {
        Camera camera = cameraSystem.getGameCamera();
        for (Integer id : sortedEntityIds) {
            process(id, camera);
        }
    }

    protected  void process(int entityId, Camera camera) {
        CAGridComponents layerStuff = CAComponent_m.get(entityId);
        //CALayer layerKey = entry.getKey();
        //CAGridBase layer = entry.getValue();

        gridFollow(camera);
    }

    protected void initStates(){
        states = new BaseCell[w][h];
        // init states. ?required?
        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < states[0].length; j++) {
                states[i][j] = newCell(0);
            }
        }
        // init states for testing
        randomizeState();
    }

    private void randomizeState() {
        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < states[0].length; j++) {
                states[i][j].setState(Math.round(Math.round(Math.random())));// round twice? one is just a cast (I think)
            }
        }
    }

    @Override
    protected  void processSystem() {
        // TODO: manage generation tasks somehow?
    }
}
