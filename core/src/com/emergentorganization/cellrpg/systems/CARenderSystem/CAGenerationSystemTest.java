package com.emergentorganization.cellrpg.systems.CARenderSystem;

import com.badlogic.gdx.graphics.Camera;
import com.emergentorganization.cellrpg.components.CAGridComponents;
import com.emergentorganization.cellrpg.core.entityfactory.CALayerFactory;
import com.emergentorganization.cellrpg.systems.CARenderSystem.layers.CALayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by 7yl4r on 12/14/2015.
 */
public class CAGenerationSystemTest {

    private final Logger logger = LogManager.getLogger(getClass());

    final int INP = 1;  // pattern insert position (upper left corner)

    final int[][] BLINKER = {
            {0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0},
            {0,0,1,1,1,0,0},
            {0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0},
    };

    final int[][] BLOCK = {
            {0,0,0,0,0,0},
            {0,0,0,0,0,0},
            {0,0,1,1,0,0},
            {0,0,1,1,0,0},
            {0,0,0,0,0,0},
            {0,0,0,0,0,0},
    };

    public void setupTest(CAGenerationSystem testSystem, CAGridComponents testComps, int[][] testPattern){
        // # 2 # init or mock any other systems the system being tested depends upon
        Camera camera = new Camera() {
            @Override
            public void update() {

            }

            @Override
            public void update(boolean updateFrustum) {

            }
        };
//        camera.viewportWidth = ???
//        camera.viewportHeight = ???

        // # 3 # set up components for the mock entity you will test the system with
        CALayerFactory.initLayerComponentsByType(testComps, CALayer.VYROIDS);

        // # 4 # manually step through the processes of the system, injecting the mock entity/system components
        testSystem._inserted(testComps, camera, false);
        testComps.stampState(testPattern, INP, INP);  // TODO: should set 50 as const
    }

    @Test
    public void testInsertOnBlock() throws Exception {
        CAGenerationSystem testSystem = new CAGenerationSystem();

        CAGridComponents testComps = new CAGridComponents();

        setupTest(testSystem, testComps, BLOCK);

        int[][] actualPattern = {
                {testComps.getState(INP-2, INP-2), testComps.getState(INP-2, INP-1),
                        testComps.getState(INP-2, INP), testComps.getState(INP-2, INP+1)},
                {testComps.getState(INP-1, INP-2), testComps.getState(INP-1, INP-1),
                        testComps.getState(INP-1, INP), testComps.getState(INP-1, INP+1)},
                {testComps.getState(INP, INP-2), testComps.getState(INP, INP-1),
                        testComps.getState(INP, INP), testComps.getState(INP, INP+1)},
                {testComps.getState(INP+1, INP-2), testComps.getState(INP+1, INP-1),
                        testComps.getState(INP+1, INP), testComps.getState(INP+1, INP+1)},
        };
        int[][] expectedPattern = {
                {0,0,0,0},
                {0,1,1,0},
                {0,1,1,0},
                {0,0,0,0}
        };
        logger.trace("block pattern: " + Arrays.deepToString(actualPattern));
        logger.trace("full pattern: " + testComps.statesToString());
        assert Arrays.deepEquals(expectedPattern, actualPattern);
    }

    @Test
    public void testGenerateOnBlinker() {
        // Comments are included to aid in future generalization of this test for testing other EntitySystems
        // # 1 # init the system we are testing
        CAGenerationSystem testSystem = new CAGenerationSystem();

        CAGridComponents testComps = new CAGridComponents();

        setupTest(testSystem, testComps, BLINKER);
        testSystem.generate(testComps);

        // # 5 # test that the components of your mock entity are what they should be after passing through the system
        int[][] actualPattern = {
                {testComps.getState(INP-1, INP-1), testComps.getState(INP-1, INP), testComps.getState(INP-1, INP+1)},
                {testComps.getState(INP  , INP-1), testComps.getState(INP  , INP), testComps.getState(INP  , INP+1)},
                {testComps.getState(INP+1, INP-1), testComps.getState(INP+1, INP), testComps.getState(INP+1, INP+1)}
        };
        int[][] expectedPattern = {
                {0,1,0},
                {0,1,0},
                {0,1,0}
        };
        logger.trace("blinker pattern: " + Arrays.deepToString(actualPattern));
        assert Arrays.deepEquals(expectedPattern, actualPattern);
    }

}
