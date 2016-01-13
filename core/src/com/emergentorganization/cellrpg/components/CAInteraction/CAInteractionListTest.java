package com.emergentorganization.cellrpg.components.CAInteraction;

import com.emergentorganization.cellrpg.events.GameEvent;
import com.emergentorganization.cellrpg.tools.CGoLShapeConsts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;


public class CAInteractionListTest {
    private final Logger logger = LogManager.getLogger(getClass());

    public static void setupInteractions(CAInteractionList testList,
                                         final int COLLIDING_LAYER_ID, final int COLLIDING_STATE) {
        testList
                .addInteraction(
                        COLLIDING_LAYER_ID,
                        new CAInteraction()
                                .addCollisionImpactStamp(COLLIDING_STATE, CGoLShapeConsts.EMPTY(6, 6), COLLIDING_LAYER_ID)
                                .addEventTrigger(COLLIDING_STATE, GameEvent.PLAYER_HIT)
                )
                .setColliderRadius(1)
        ;
    }

    @Test
    public void testInteractionBuilderAddsCollidingStates() {
        final int COLLIDING_LAYER_ID = 777;
        final int COLLIDING_STATE = 9;

        CAInteractionList testInteractions = new CAInteractionList();

        setupInteractions(testInteractions, COLLIDING_LAYER_ID, COLLIDING_STATE);

        ArrayList collides = testInteractions.interactions.get(COLLIDING_LAYER_ID).getCollidingStates();

        // check that colliding state has been added
        logger.trace("colliding states: " + collides);
        assert collides.get(0).equals(COLLIDING_STATE);
    }
}
