package io.github.emergentorganization.cellrpg.systems.CASystems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;
import io.github.emergentorganization.cellrpg.components.CAGridComponents;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAs.CA;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAs.iCA;
import io.github.emergentorganization.emergent2dcore.systems.CameraSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Handles CA grid state generations and initialization.
 */
public class CAGenerationSystem extends BaseEntitySystem {
    private static final EnumMap<CA, iCA> CAs = CA.getCAMap();
    private final Logger logger = LogManager.getLogger(getClass());
    // artemis-injected entity components:
    private ComponentMapper<CAGridComponents> CAComponent_m;
    private CameraSystem cameraSystem;

    public CAGenerationSystem() {
        super(Aspect.all(CAGridComponents.class));
    }

    @Override
    protected void processSystem() {
        IntBag idBag = getEntityIds();
        for (int index = 0; index < idBag.size(); index++) {
            int id = idBag.get(index);
            process(id);
        }
    }

    protected void process(int entityId) {
        CAGridComponents layerStuff = CAComponent_m.get(entityId);

        // TODO: manage generation tasks somehow?

    }

    @Override
    protected void inserted(int entityId) {
        super.inserted(entityId);
        _inserted(CAComponent_m.get(entityId), true);
    }

    public void _inserted(CAGridComponents layerStuff, Boolean autoGenerate) {
        // :param autoGenerate: if true starts generation loop on separate thread,
        //                      if false generations must be manually handled using CAGenerationSystem.generate()
        // _TEST : this inner method is separated from the entity-component system manager, that is, all components
        //              are passed directly into this method. This is to enable unit testing of the system.

        if (autoGenerate) {
            initGenerationLoop(layerStuff);
        }
    }

    public void generate(CAGridComponents gridComps) {
        // generates next state of the CA
        gridComps.generation += 1;
        CAs.get(gridComps.ca).generate(gridComps);
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

    private void initGenerationLoop(CAGridComponents layerComponents) {
        // sets up generation timer loop on separate thread
        scheduleGeneration(0, layerComponents);
    }

    public void scheduleGeneration(long runtime, CAGridComponents layComp) {
        // schedules a new generation thread
        Timer time = new Timer();
        // add runtime to TIME_BTWN_GENERATIONS to ensure this thread never uses more than 50% CPU time
        long genTime = layComp.TIME_BTWN_GENERATIONS + runtime;
        if (genTime > layComp.maxGenTime) {
            layComp.maxGenTime = genTime;
        } else if (genTime < layComp.minGenTime) {
            layComp.minGenTime = genTime;
        }
        time.schedule(new GenerateTask(this, layComp), genTime);
    }

    private void randomizeState(CAGridComponents gridComponents) {
        for (int i = 0; i < gridComponents.states.length; i++) {
            for (int j = 0; j < gridComponents.states[0].length; j++) {
                int val = Math.round(Math.round(Math.random()));
                if (val != 0) {
                    gridComponents.cellCount += 1;
                    gridComponents.states[i][j].setState(val);// round twice? one is just a cast (I think)
                }
            }
        }
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
}
