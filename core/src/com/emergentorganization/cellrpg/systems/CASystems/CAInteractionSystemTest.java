package com.emergentorganization.cellrpg.systems.CASystems;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.emergentorganization.cellrpg.components.CAGridComponents;
import com.emergentorganization.cellrpg.components.CAInteraction.CAInteraction;
import com.emergentorganization.cellrpg.components.CAInteraction.CAInteractionList;
import com.emergentorganization.cellrpg.components.CAInteraction.CAInteractionListTest;
import com.emergentorganization.cellrpg.components.Position;
import com.emergentorganization.cellrpg.core.entityfactory.CALayerFactory;
import com.emergentorganization.cellrpg.events.GameEvent;
import com.emergentorganization.cellrpg.systems.CASystems.layers.CALayer;
import com.emergentorganization.cellrpg.tools.CGoLShapeConsts;
import com.emergentorganization.cellrpg.tools.testUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by 7yl4r on 12/14/2015.
 */
public class CAInteractionSystemTest {

    private final Logger logger = LogManager.getLogger(getClass());

    private final int COLLIDING_STATE = 1;

    private final int COLLIDING_LAYER_ID = 1;

    private void setupTestState_full(CAGridComponents testComps){
        // sets up given component with a full CA grid
        Camera camera = new Camera() {
            @Override
            public void update() {

            }

            @Override
            public void update(boolean updateFrustum) {

            }
        };

        CALayerFactory.initLayerComponentsByType(testComps, CALayer.VYROIDS, camera);

        testComps.fill(COLLIDING_STATE);
    }

    @Test
    public void testCollideOnFullCA() {
        // tests collision on a ca grid that is 100% full of colliding cells
        CAInteractionSystem testSystem = new CAInteractionSystem();

        CAGridComponents testGridComps = new CAGridComponents();
        CAInteractionList testInteractions = new CAInteractionList();
        Position testPos = new Position();

        CAInteractionListTest.setupInteractions(testInteractions, COLLIDING_LAYER_ID, COLLIDING_STATE);

        setupTestState_full(testGridComps);

        testSystem._inserted(testPos, testInteractions);

        Boolean res = testSystem.checkCollideAt(
                testPos.position,
                testInteractions.interactions.get(COLLIDING_LAYER_ID),
                testGridComps);

        logger.trace(
                "collision @ " + testPos.position
                + " (state=" + testGridComps.getState(testPos.position) + ")? "
                + res
        );

        assert res == true;
    }
}
