package com.emergentorganization.cellrpg.components;

import com.badlogic.gdx.graphics.Camera;
import com.emergentorganization.cellrpg.core.entityfactory.CALayerFactory;
import com.emergentorganization.cellrpg.systems.CASystems.CAGenerationSystem;
import com.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import com.emergentorganization.cellrpg.tools.CGoLShapeConsts;
import com.emergentorganization.cellrpg.tools.testUtils;
import org.junit.Test;


public class CAGridComponentsTest {
    @Test
    public void testStampBlock(){
        Camera camera = new Camera() {
            @Override
            public void update() {

            }

            @Override
            public void update(boolean updateFrustum) {

            }
        };

        CAGridComponents testComps = new CAGridComponents();
        CALayerFactory.initLayerComponentsByType(testComps, CALayer.VYROIDS, camera);
        CAGenerationSystem testSystem = new CAGenerationSystem();

        testSystem._inserted(testComps, false);
        testComps.stampState(CGoLShapeConsts.BLOCK, 0, 0);
        assert testUtils.ifStatesMatchAt(testComps, CGoLShapeConsts.BLOCK, 0, 0);
    }
}
