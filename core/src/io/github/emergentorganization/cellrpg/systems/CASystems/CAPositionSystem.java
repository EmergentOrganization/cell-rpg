package io.github.emergentorganization.cellrpg.systems.CASystems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.Camera;
import io.github.emergentorganization.cellrpg.components.CAGridComponents;
import io.github.emergentorganization.cellrpg.core.systems.CameraSystem;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAs.CACell.BaseCell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * system for positioning the CA layers so that they appear static
 * while also shifting the origin/center of the generation window's
 * CA state array.
 * <p/>
 * Adapted from CAGridBase by 7yl4r on 2015-12-14.
 */
public class CAPositionSystem extends BaseEntitySystem {
    private final Logger logger = LogManager.getLogger(getClass());
    // artemis-injected entity components:
    private CameraSystem cameraSystem;
    private ComponentMapper<CAGridComponents> CAComponent_m;

    public CAPositionSystem() {
        super(Aspect.all(CAGridComponents.class));
    }

    @Override
    protected void processSystem() {
        IntBag entityBag = getEntityIds();
        for (int i = 0; i < entityBag.size(); i++) {
            int id = entityBag.get(i);
            process(id);
        }
    }

    private void process(int entityId) {
        CAGridComponents gridComps = CAComponent_m.get(entityId);
        gridFollow(gridComps, cameraSystem.getGameCamera());
    }

    private void addColLeft(CAGridComponents gridComps) {
        // pushes a column onto left side
        for (int i = gridComps.states.length - 1; i >= 0; i--) {
            for (int j = 0; j < gridComps.states[0].length; j++) {
                if (i == 0) {
                    gridComps.states[i][j] = getEdgeState(j, gridComps);
                } else {
                    gridComps.states[i][j] = gridComps.states[i - 1][j];
                }
            }
        }
        gridComps.gridOriginX -= gridComps.cellSize + 1;
    }

    private void addColRight(CAGridComponents gridComps) {
        // pushes a column onto right side
        for (int i = 0; i < gridComps.states.length; i++) {
            for (int j = 0; j < gridComps.states[0].length; j++) {
                if (i == gridComps.states.length - 1) {
                    gridComps.states[i][j] = getEdgeState(j, gridComps);
                } else {
                    gridComps.states[i][j] = gridComps.states[i + 1][j];
                }
            }
        }
        gridComps.gridOriginX += gridComps.cellSize + 1;
    }

    private void addRowBottom(CAGridComponents gridComps) {
        // pushes a column of zeros
        for (int i = 0; i < gridComps.states.length; i++) {
            for (int j = gridComps.states[0].length - 1; j >= 0; j--) {
                if (j == 0) {
                    gridComps.states[i][j] = getEdgeState(i, gridComps);
                } else {
                    gridComps.states[i][j] = gridComps.states[i][j - 1];
                }
            }
        }
        gridComps.gridOriginY -= gridComps.cellSize + 1;
    }

    private void addRowTop(CAGridComponents gridComps) {
        // pushes a column of zeros
        for (int i = 0; i < gridComps.states.length; i++) {
            for (int j = 0; j < gridComps.states[0].length; j++) {
                if (j == gridComps.states[0].length - 1) {
                    gridComps.states[i][j] = getEdgeState(i, gridComps);
                } else {
                    gridComps.states[i][j] = gridComps.states[i][j + 1];
                }
            }
        }
        gridComps.gridOriginY += gridComps.cellSize + 1;
    }

    private BaseCell getEdgeState(int x, CAGridComponents gridComps) {
        // returns cell state in position x on a newly added edge
        int state;
        if (gridComps.edgeSpawner == CAEdgeSpawnType.RANDOM_SPARSE) {
            state = getRandomState(0.05f);
        } else if (gridComps.edgeSpawner == CAEdgeSpawnType.RANDOM_50_50) {
            state = getRandomState(.5f);
        } else if (gridComps.edgeSpawner == CAEdgeSpawnType.RANDOM_DENSE) {
            state = getRandomState(.8f);
        } else if (gridComps.edgeSpawner == CAEdgeSpawnType.EMPTY) {
            state = 0;
        } else {
            throw new IllegalStateException("edgeSpawn type not recognized");
        }
        return gridComps.newCell(state);
    }

    private void gridFollow(CAGridComponents gridComps, Camera camera) {
        // enables grid to follow the camera

        float dY = gridComps.gridOriginY - camera.position.y / gridComps.SCALE;
//        logger.trace(dY + "=" + gridOriginY + "-" + camera.position.y + "/" + scale);

        while (dY > gridComps.cellSize + 1) {
            logger.trace("BotAddRow");
            addRowBottom(gridComps);
            dY = gridComps.gridOriginY - camera.position.y / gridComps.SCALE;
            //logger.trace(dY + "=" + gridOriginY + "-" + camera.position.y + "/" + scale);
        }
        while (dY < -gridComps.cellSize + 1) {
            logger.trace("TopAddRow");
            addRowTop(gridComps);
            dY = gridComps.gridOriginY - camera.position.y / gridComps.SCALE;
            //logger.trace(dY + "=" + gridOriginY + "-" + camera.position.y + "/" + scale);
        }

        float dX = gridComps.gridOriginX - camera.position.x / gridComps.SCALE;

        while (dX > gridComps.cellSize + 1) {
            addColLeft(gridComps);
            dX = gridComps.gridOriginX - camera.position.x / gridComps.SCALE;
        }
        while (dX < -gridComps.cellSize + 1) {
            addColRight(gridComps);
            dX = gridComps.gridOriginX - camera.position.x / gridComps.SCALE;
        }

        logger.trace("camera is (" + dX + "," + dY + ") from grid origin.");
    }

    private int getRandomState(float percentLive) {
        if (Math.random() > percentLive) {
            return 0;
        } else {
            return 1;
        }
    }
}
