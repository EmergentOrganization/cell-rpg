package com.emergentorganization.cellrpg.systems.CARenderSystem;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.Camera;
import com.emergentorganization.cellrpg.components.CAGridComponents;
import com.emergentorganization.cellrpg.systems.CARenderSystem.CACell.BaseCell;

/**
 * Handles CA grid state generations and initialization.
 *
 * Created by 7yl4r on 12/9/2015.
 */
public class CAGenerationSystem extends BaseEntitySystem {

    // artemis-injected entity components:
    private ComponentMapper<CAGridComponents> CAComponent_m;

    public CAGenerationSystem(){
        super(Aspect.all(CAGridComponents.class));
    }

    @Override
    protected  void processSystem() {

        //Camera camera = cameraSystem.getGameCamera();
        IntBag idBag = getEntityIds();
        for (int index = 0; index < idBag.size(); index ++ ) {
            int id = idBag.get(index);
            process(id);
        }
    }

    protected  void process(int entityId) {
        CAGridComponents layerStuff = CAComponent_m.get(entityId);

        // TODO: manage generation tasks somehow?

    }

    @Override
    protected void inserted(int entityId) {
        super.inserted(entityId);

        CAGridComponents layerStuff = CAComponent_m.get(entityId);

        initStates(layerStuff);
    }

    protected void initStates(CAGridComponents gridComponents){
        gridComponents.states = new BaseCell[gridComponents.getSizeX()][gridComponents.getSizeY()];
        // init states. ?required?
        for (int i = 0; i < gridComponents.states.length; i++) {
            for (int j = 0; j < gridComponents.states[0].length; j++) {
                gridComponents.states[i][j] = gridComponents.newCell(0);
            }
        }
        // init states for testing
        randomizeState(gridComponents.states);
    }

    private void randomizeState(BaseCell[][] states) {
        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < states[0].length; j++) {
                states[i][j].setState(Math.round(Math.round(Math.random())));// round twice? one is just a cast (I think)
            }
        }
    }
}
