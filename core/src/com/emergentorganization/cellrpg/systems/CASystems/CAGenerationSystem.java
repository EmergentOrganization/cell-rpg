package com.emergentorganization.cellrpg.systems.CASystems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.Camera;
import com.emergentorganization.cellrpg.components.CAGridComponents;
import com.emergentorganization.cellrpg.systems.CASystems.CACell.BaseCell;
import com.emergentorganization.cellrpg.systems.CASystems.CACell.CellWithHistory;
import com.emergentorganization.cellrpg.systems.CASystems.CACell.GeneticCell;
import com.emergentorganization.cellrpg.systems.CameraSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
        _inserted(CAComponent_m.get(entityId), cameraSystem.getGameCamera(), true);
    }

    public void _inserted(CAGridComponents layerStuff, Camera camera,
                             Boolean autoGenerate){
        // :param autoGenerate: if true starts generation loop on separate thread,
        //                      if false generations must be manually handled using CAGenerationSystem.generate()
        // _TEST : this inner method is separated from the entity-component system manager, that is, all components
        //              are passed directly into this method. This is to enable unit testing of the system.

        int sx = (int) (camera.viewportWidth)  + 2*layerStuff.OFF_SCREEN_PIXELS;
        int sy = (int) (camera.viewportHeight) + 2*layerStuff.OFF_SCREEN_PIXELS;

        int w = sx / (layerStuff.cellSize + 1);  // +1 for border pixel between cells
        int h = sy / (layerStuff.cellSize + 1);

        logger.info("initializing CAGrid " + w + "(" + sx + "px)x" + h + "(" + sy + "px). cellSize=" + layerStuff.cellSize);

        initStates(layerStuff, w, h);

        if (autoGenerate) {
            initGenerationLoop(layerStuff);
        }
    }

    protected void generate(CAGridComponents gridComps){
        // generates next state of the CA
        gridComps.generation += 1;

        // TODO: using an interface, enum, and map like w/ ICellRenderer is prettier.
        switch (gridComps.cellType){
            case WITH_HISTORY:
                generate_buffered(gridComps);
                break;
            case DECAY:
                generate_decay(gridComps);
                break;
            case GENETIC:
                generate_genetic(gridComps);
            case BASE:
            default:
                generate_NoBuffer(gridComps);
        }
    }

    // NOTE: for generate_genetic only:
    enum CellAction{
        DIE,   // living cell -> dead cell
        SPAWN, // dead  cell -> living cell
        NONE  // no change to cell state
    }

    // NOTE: for generate_genetic only:
    protected CellAction ca_rule(final int neighborCount) {
        // Conway's Game of Life:
        switch (neighborCount) {
            case 2:
                return CellAction.NONE;
            case 3:
                return CellAction.SPAWN;
            default:
                return CellAction.DIE;
        }
    }

    // NOTE: for generate_genetic only:
    private ArrayList<GeneticCell> getLiveParentsOf(int row, int col, int neighborhoodSize, CAGridComponents gridComps){
        // returns list of live cells surrounding cell
        // size must be odd!
        ArrayList<GeneticCell> parents = new ArrayList<GeneticCell>();
        final boolean SKIP_SELF = true;

        // checkSize(size);
        final int radius = (neighborhoodSize - 1) / 2;
        for (int i = row - radius; i <= row + radius; i++) {
            for (int j = col - radius; j <= col + radius; j++) {
                if (SKIP_SELF && i - row + radius == j - col + radius && i - row + radius == radius) {
                    continue;
                } else {
                    try {
                        GeneticCell cell = (GeneticCell) gridComps.states[i][j];
                        if (cell.state > 0) {
                            parents.add(cell);
                        }  // else cell is not alive, exclude
                    } catch(ArrayIndexOutOfBoundsException err){
                        // cell is on edge of grid, trying to access off-grid neighbor as potential parent
                        // ignore
                    }
                }
            }
        }
        return parents;
    }

    protected void generate_genetic(CAGridComponents gridComps) {
        // count up all neighbors
        for (int i = 0; i < gridComps.states.length; i++) {
            for (int j = 0; j < gridComps.states[0].length; j++) {
                GeneticCell cell = (GeneticCell) gridComps.states[i][j];
                cell.neighborCount = getNeighborhoodSum(i, j, 3, gridComps);
                gridComps.states[i][j] = cell;
            }
        }

        // act on neighbor counts
        for (int i = 0; i < gridComps.states.length; i++) {
            for (int j = 0; j < gridComps.states[0].length; j++) {
                GeneticCell cell = (GeneticCell) gridComps.states[i][j];
                cell.dgrn.tick();
                CellAction act = ca_rule(cell.neighborCount);
//                if (cell.state > 0) {
//                    System.out.println(cell.neighborCount + "->" + act.toString());
//                }
                switch(act){
                    case SPAWN:
                        if (cell.state == 0 ){
                            try {
                                gridComps.states[i][j] = new GeneticCell(1, getLiveParentsOf(i, j, 3, gridComps)).incubate();
                            } catch (IllegalStateException ex){
                                // not enough parents
                                gridComps.states[i][j] = gridComps.newCell(1);
                            }
                        }
                        break;
                    case DIE:
                        gridComps.setState(i, j, 0);
                        break;
                    default:
                        break;
                }
            }
        }

        // change up the lastBuilder periodically. This adds some variety to the random spawns.
        gridComps.lastBuilderStamp -= 1;
    }


    protected void generate_decay(CAGridComponents gridComps) {
        for (int i = 0; i < gridComps.states.length; i++) {
            for (int j = 0; j < gridComps.states[0].length; j++) {
                gridComps.states[i][j].setState(gridComps.states[i][j].getState() - 1);
            }
        }
    }

    protected void generate_NoBuffer(CAGridComponents gridComps) {
        for (int i = 0; i < gridComps.states.length; i++) {
            for (int j = 0; j < gridComps.states[0].length; j++) {
                gridComps.states[i][j].setState(ca_rule(i, j, gridComps));
            }
        }
    }

    protected void generate_buffered(CAGridComponents gridComps) {
        // generates the next frame of the CA
//        logger.info("gen buffered");
        for (int i = 0; i < gridComps.states.length; i++) {
            for (int j = 0; j < gridComps.states[0].length; j++) {
                CellWithHistory cell = (CellWithHistory) gridComps.states[i][j];
                cell.setLastState(gridComps.states[i][j].getState());
            }
        }

        for (int i = 0; i < gridComps.states.length; i++) {
            for (int j = 0; j < gridComps.states[0].length; j++) {
                //System.out.print(i + "," + j +'\t');
                gridComps.states[i][j].setState(ca_rule(i, j, gridComps));
            }
        }
    }

    protected int ca_rule(final int row, final int col, CAGridComponents gridComps){
        // TODO: check gridComps.CARule and execute appropriate rule
        return ca_rule_CGoL(row, col, gridComps);
    }

    protected int ca_rule_CGoL(final int row, final int col, CAGridComponents gridComps) {
        // computes the rule at given row, col in cellStates array, returns result
        // Conway's Game of Life:
        switch (getNeighborhoodSum(row, col, 3, gridComps)) {
            case 2:
                return gridComps.getState(row, col);
            case 3:
                return 1;
            default:
                return 0;
        }
        // random state:
        //return Math.round(Math.round(Math.random()));  // round twice? one is just a cast (I think)
    }

    private int[][] getNeighborhood(final int row, final int col, final int size, CAGridComponents gridComps) {
        // returns matrix of neighborhood around (row, col) with edge size "size"
        // size MUST be odd! (not checked for efficiency)

        // checkSize(size);
        int radius = (size - 1) / 2;
        int[][] neighbors = new int[size][size];
        for (int i = row - radius; i <= row + radius; i++) {
            int neighbor_i = i - row + radius;
            for (int j = col - radius; j <= col + radius; j++) {
                int neighbor_j = j - col + radius;
                neighbors[neighbor_i][neighbor_j] = gridComps.getState(i, j);
            }
        }
        return neighbors;
    }

    public int getNeighborhoodSum(final int row, final int col, final int size, CAGridComponents gridComps) {
        // returns sum of all states in neighborhood
        // size MUST be odd! (not checked for efficiency)
        final boolean SKIP_SELF = true;

        // checkSize(size);
        final int radius = (size - 1) / 2;
        int sum = 0;
        for (int i = row - radius; i <= row + radius; i++) {
            for (int j = col - radius; j <= col + radius; j++) {
                if (SKIP_SELF && i - row + radius == j - col + radius && i - row + radius == radius) {
                    continue;
                } else {
                    sum += gridComps.getLastState(i, j);
                }
            }
        }
//        logger.info("count: " + sum);
        return sum;
    }

    private float getKernelizedValue(final int[][] kernel, final int row, final int col, final int size, CAGridComponents gridComps) {
        // returns value from applying the kernel matrix to the given neighborhood
        final boolean SKIP_SELF = true;

        // checkSize(size);
        final int radius = (size - 1) / 2;
        int sum = 0;
        for (int i = row - radius; i <= row + radius; i++) {
            int kernel_i = i - row + radius;
            for (int j = col - radius; j <= col + radius; j++) {
                int kernel_j = j - row + radius;
                if (SKIP_SELF && kernel_i == kernel_j && kernel_i == radius) {
                    continue;
                } else {
                    sum += gridComps.getState(i, j) * kernel[kernel_i][kernel_j];
                }
            }
        }
        return sum;
    }

    private float get3dKernelizedValue(final int[][][] kernel, final int row, final int col, final int size, CAGridComponents gridComps) {
        // returns value from applying the kernel matrix to the given neighborhood
        // 3rd dimension of the kernel is state-space (ie, kernel[x][y][state])
        final boolean SKIP_SELF = true;

        // checkSize(size);
        final int radius = (size - 1) / 2;
        int sum = 0;
        for (int i = row - radius; i <= row + radius; i++) {
            int kernel_i = i - row + radius;
            for (int j = col - radius; j <= col + radius; j++) {
                int kernel_j = j - row + radius;
                if (SKIP_SELF && kernel_i == kernel_j && kernel_i == radius) {
                    continue;
                } else {
                    try {
                        sum += gridComps.getState(i, j) * kernel[kernel_i][kernel_j][gridComps.getState(i, j)];
                    } catch (IndexOutOfBoundsException err) {
                        throw new IndexOutOfBoundsException(
                                "kernel has no value for [" + kernel_i + "," + kernel_j + ","
                                + gridComps.getState(i, j) + "]"
                        );
                    }
                }
            }
        }
        return sum;
    }

    private void initGenerationLoop(CAGridComponents layerComponents){
        // sets up generation timer loop on separate thread
        scheduleGeneration(0, layerComponents);
    }

    public void scheduleGeneration(long runtime, CAGridComponents layComp){
        // schedules a new generation thread
        Timer time = new Timer();
        // add runtime to TIME_BTWN_GENERATIONS to ensure this thread never uses more than 50% CPU time
        long genTime = layComp.TIME_BTWN_GENERATIONS + runtime;
        if( genTime > layComp.maxGenTime){
            layComp.maxGenTime = genTime;
        } else if (genTime < layComp.minGenTime){
            layComp.minGenTime = genTime;
        }
        time.schedule(new GenerateTask(this, layComp), genTime);
    }

    class GenerateTask extends TimerTask {
        private CAGenerationSystem genSys;
        private CAGridComponents gridComp;

        public GenerateTask(CAGenerationSystem _genSys, CAGridComponents _gridComp) {
            genSys = _genSys;
            gridComp = _gridComp;
        }

        public void run() {
            try {
                long runtime = System.currentTimeMillis();
                genSys.generate(gridComp);

                runtime = System.currentTimeMillis() - runtime;
                genSys.scheduleGeneration(runtime, gridComp);
                Thread.currentThread().stop();
                return;
            } catch (Exception ex) {
                logger.error("error running CA generation thread: " + ex.getMessage());
                Thread.currentThread().stop();
                return;
            }
        }
    }

    protected void initStates(CAGridComponents gridComponents, int w, int h){
        gridComponents.states = new BaseCell[w][h];
        // init states. ?required?
        for (int i = 0; i < gridComponents.states.length; i++) {
            for (int j = 0; j < gridComponents.states[0].length; j++) {
                gridComponents.states[i][j] = gridComponents.newCell(0);
            }
        }
        // init states for testing
        //randomizeState(gridComponents);
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
