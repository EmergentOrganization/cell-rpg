package com.emergentorganization.cellrpg.systems.CARenderSystem;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.Camera;
import com.emergentorganization.cellrpg.components.CAGridComponents;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.managers.AssetManager;
import com.emergentorganization.cellrpg.systems.CARenderSystem.CACell.BaseCell;
import com.emergentorganization.cellrpg.systems.CameraSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles CA grid state generations and initialization.
 *
 * Created by 7yl4r on 12/9/2015.
 */
public class CAGenerationSystem extends BaseEntitySystem {
    private final Logger logger = LogManager.getLogger(getClass());

    // artemis-injected entity components:
    private ComponentMapper<CAGridComponents> CAComponent_m;
    private CameraSystem cameraSystem;


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
        Camera camera = cameraSystem.getGameCamera();

        int sx = (int) (camera.viewportWidth)  + 2*layerStuff.OFF_SCREEN_PIXELS;
        int sy = (int) (camera.viewportHeight) + 2*layerStuff.OFF_SCREEN_PIXELS;

        int w = sx / (layerStuff.cellSize + 1);  // +1 for border pixel between cells
        int h = sy / (layerStuff.cellSize + 1);

        logger.info("initializing CAGrid " + w + "(" + sx + "px)x" + h + "(" + sy + "px). cellSize="+layerStuff.cellSize);

        initStates(layerStuff, w, h);
        // TODO: initGenerationLoop();
    }

    protected void initStates(CAGridComponents gridComponents, int w, int h){
        gridComponents.states = new BaseCell[w][h];
        //gridComponents.states = new BaseCell[gridComponents.getSizeX()][gridComponents.getSizeY()];
        // init states. ?required?
        for (int i = 0; i < gridComponents.states.length; i++) {
            for (int j = 0; j < gridComponents.states[0].length; j++) {
                gridComponents.states[i][j] = gridComponents.newCell(0);
            }
        }
        // init states for testing
        randomizeState(gridComponents);
    }

    private void randomizeState(CAGridComponents gridComponents) {
        for (int i = 0; i < gridComponents.states.length; i++) {
            for (int j = 0; j < gridComponents.states[0].length; j++) {
                int val = Math.round(Math.round(Math.random()));
                if (val != 0){
                    gridComponents.cellCount += 1;
                    gridComponents.states[i][j].setState(val);// round twice? one is just a cast (I think)
                }
            }
        }
    }
}
