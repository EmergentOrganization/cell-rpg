package io.github.emergentorganization.cellrpg.components;

import com.badlogic.gdx.graphics.Camera;
import io.github.emergentorganization.cellrpg.core.entityfactory.CALayerFactory;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAGenerationSystem;
import io.github.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import io.github.emergentorganization.cellrpg.tools.CGoLShapeConsts;
import io.github.emergentorganization.cellrpg.tools.testUtils;
import org.junit.Test;


public class CAGridComponentsTest {
    @Test
    public void testStampBlock() {
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

        testSystem._inserted(testComps, false, 0);
        testComps.stampState(CGoLShapeConsts.BLOCK, 0, 0);
        assert testUtils.ifStatesMatchAt(testComps, CGoLShapeConsts.BLOCK, 0, 0);
    }
}
