package io.github.emergentorganization.cellrpg.systems.CASystems;

import com.badlogic.gdx.graphics.Camera;
import io.github.emergentorganization.cellrpg.components.CAGridComponents;
import io.github.emergentorganization.cellrpg.core.entityfactory.CALayerFactory;
import io.github.emergentorganization.cellrpg.managers.EventManager;
import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import io.github.emergentorganization.cellrpg.tools.CGoLShapeConsts;
import io.github.emergentorganization.cellrpg.tools.testUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;


public class CAGenerationSystemTest {

    private final int INP = 0;  // pattern insert position (upper left corner)
    private final Logger logger = LogManager.getLogger(getClass());

    private void setupTest(CAGenerationSystem testSystem, CAGridComponents testComps, int[][] testPattern) {
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
        testSystem._inserted(testComps, false, 0);

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
        EventManager eventManager = new EventManager();
        testSystem._generate(eventManager, testComps, 0);

        // # 5 # test that the components of your mock entity are what they should be after passing through the system
        assert testUtils.ifStatesMatchAt(testComps, CGoLShapeConsts.BLINKER_V, INP, INP);
    }

}
