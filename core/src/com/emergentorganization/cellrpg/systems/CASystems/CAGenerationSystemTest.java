package com.emergentorganization.cellrpg.systems.CASystems;

import com.badlogic.gdx.graphics.Camera;
import com.emergentorganization.cellrpg.components.CAGridComponents;
import com.emergentorganization.cellrpg.core.entityfactory.CALayerFactory;
import com.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import com.emergentorganization.cellrpg.tools.CGoLShapeConsts;
import com.emergentorganization.cellrpg.tools.testUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

/**
 * Created by 7yl4r on 12/14/2015.
 */
public class CAGenerationSystemTest {

    private final Logger logger = LogManager.getLogger(getClass());

    final int INP = 0;  // pattern insert position (upper left corner)

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
        CALayerFactory.initLayerComponentsByType(testComps, CALayer.VYROIDS, camera);

        // # 4 # manually step through the processes of the system, injecting the mock entity/system components
        testSystem._inserted(testComps, false);

        // test
        testComps.stampState(testPattern, INP, INP);
    }


    @Test
    public void testInsertOnBlock() {
        CAGenerationSystem testSystem = new CAGenerationSystem();

        CAGridComponents testComps = new CAGridComponents();

        setupTest(testSystem, testComps, CGoLShapeConsts.BLOCK);

        logger.trace("full pattern: " + testComps.statesToString());
        assert testUtils.ifStatesMatchAt(testComps, CGoLShapeConsts.BLOCK, INP, INP);
    }

    @Test
    public void testGenerateOnBlinker() {
        // Comments are included to aid in future generalization of this test for testing other EntitySystems
        // # 1 # init the system we are testing
        CAGenerationSystem testSystem = new CAGenerationSystem();

        CAGridComponents testComps = new CAGridComponents();

        setupTest(testSystem, testComps, CGoLShapeConsts.BLINKER_H);
        testSystem.generate(testComps);

        // # 5 # test that the components of your mock entity are what they should be after passing through the system
        assert testUtils.ifStatesMatchAt(testComps, CGoLShapeConsts.BLINKER_V, INP, INP);
    }

}
