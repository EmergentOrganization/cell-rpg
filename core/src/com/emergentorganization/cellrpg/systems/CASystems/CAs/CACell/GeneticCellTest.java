package com.emergentorganization.cellrpg.systems.CASystems.CAs.CACell;

import com.badlogic.gdx.graphics.Color;
import com.emergentorganization.cellrpg.systems.CASystems.GeneticCells.DGRN4j.DGRN;
import com.emergentorganization.cellrpg.systems.CASystems.GeneticCells.DGRN4j.GraphInitializer;
import com.emergentorganization.cellrpg.systems.CASystems.GeneticCells.GeneticCellBuilders.MockBuilder;
import com.emergentorganization.cellrpg.systems.CASystems.GeneticCells.GeneticCellBuilders.TestCell1;
import com.emergentorganization.cellrpg.systems.CASystems.GeneticCells.GeneticCellBuilders.TestCell2;
import junit.framework.Assert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import javax.xml.crypto.KeySelectorException;


public class GeneticCellTest {
    public static final String TEST_INNER_NODE_ID_1 = "test inner node 1";
    private final Logger logger = LogManager.getLogger(getClass());

    @Test
    public void testActivationPropagation() throws Exception {
        TestCell2 cell_builder = new TestCell2();
        logger.debug("cell builder:" + cell_builder);
        GeneticCell testCell = new GeneticCell(1, cell_builder);
        logger.debug("test cell:" + testCell);
        // t=0, states: 1 -> 0 -> 0
        int TF = Integer.parseInt(DGRN.getNodeAttributeValue(
                testCell.dgrn.getNode(GeneticCell.inflowNodes.ALWAYS_ON),
                GeneticCell.nodeAttribute.ACTIVATION_VALUE
        ));
        assert TF > 0;
        TF = Integer.parseInt(DGRN.getNodeAttributeValue(
                testCell.dgrn.getNode(TEST_INNER_NODE_ID_1),
                GeneticCell.nodeAttribute.ACTIVATION_VALUE
        ));
        assert TF == 0;

        TF = Integer.parseInt(DGRN.getNodeAttributeValue(
                testCell.dgrn.getNode(GeneticCell.outflowNodes.COLOR_LIGHTEN),
                GeneticCell.nodeAttribute.ACTIVATION_VALUE
        ));
        assert TF == 0;

        testCell.dgrn.tick();
        // t=1, states: 1 -> 1 -> 0
        TF = Integer.parseInt(DGRN.getNodeAttributeValue(
                testCell.dgrn.getNode(GeneticCell.inflowNodes.ALWAYS_ON),
                GeneticCell.nodeAttribute.ACTIVATION_VALUE
        ));
        assert TF > 0;

        TF = Integer.parseInt(DGRN.getNodeAttributeValue(
                testCell.dgrn.getNode(TEST_INNER_NODE_ID_1),
                GeneticCell.nodeAttribute.ACTIVATION_VALUE
        ));
        assert TF == 1;

        TF = Integer.parseInt(DGRN.getNodeAttributeValue(
                testCell.dgrn.getNode(GeneticCell.outflowNodes.COLOR_LIGHTEN),
                GeneticCell.nodeAttribute.ACTIVATION_VALUE
        ));
        assert TF == 0;

        testCell.dgrn.tick();
        // t=2, states: 1 -> 2 -> 0
        TF = Integer.parseInt(DGRN.getNodeAttributeValue(
                testCell.dgrn.getNode(GeneticCell.inflowNodes.ALWAYS_ON),
                GeneticCell.nodeAttribute.ACTIVATION_VALUE
        ));
        assert TF > 0;

        TF = Integer.parseInt(DGRN.getNodeAttributeValue(
                testCell.dgrn.getNode(TEST_INNER_NODE_ID_1),
                GeneticCell.nodeAttribute.ACTIVATION_VALUE
        ));
        Assert.assertEquals(2, TF);

        TF = Integer.parseInt(DGRN.getNodeAttributeValue(
                testCell.dgrn.getNode(GeneticCell.outflowNodes.COLOR_LIGHTEN),
                GeneticCell.nodeAttribute.ACTIVATION_VALUE
        ));
        Assert.assertEquals(0, TF);
    }

    @Test
    public void testCellStateIsSetByConstructor() {
        int INIT_STATE = 0;
        GeneticCell testCell = new GeneticCell(INIT_STATE, new MockBuilder());
        Assert.assertEquals("initial state should be set to " + INIT_STATE, INIT_STATE, testCell.getState());
    }

    @Test
    public void testDefaultGraphHasAlwaysOnNode() throws KeySelectorException {
        GeneticCell testCell = new GeneticCell(0, new MockBuilder());
        testCell.dgrn.getNode(GeneticCell.inflowNodes.ALWAYS_ON);
    }

    @Test
    public void testDefaultGraphAlwaysOnNodeIsActive() throws Exception {
        GeneticCell testCell = new GeneticCell(0, new MockBuilder());
        GraphInitializer.buildLightenCellTestGraph(testCell.dgrn);
        int TF = Integer.parseInt(DGRN.getNodeAttributeValue(
                testCell.dgrn.getNode(GeneticCell.inflowNodes.ALWAYS_ON),
                GeneticCell.nodeAttribute.ACTIVATION_VALUE
        ));
        if (TF < 1) {
            throw new Exception("alwaysOn.TF should be >= 1, found:" + TF);
        }
    }

    @Test
    public void testAlwaysOnStaysActive() throws Exception {
        GeneticCell testCell = new GeneticCell(1, new TestCell1());
        // check activation value
        int TF = Integer.parseInt(DGRN.getNodeAttributeValue(
                testCell.dgrn.getNode(GeneticCell.inflowNodes.ALWAYS_ON),
                GeneticCell.nodeAttribute.ACTIVATION_VALUE
        ));
        if (TF < 1) {
            throw new Exception("alwaysOn.TF should be > 1, found:" + TF);
        }

        // one tick later
        testCell.dgrn.tick();
        // check activation value
        TF = Integer.parseInt(DGRN.getNodeAttributeValue(
                testCell.dgrn.getNode(GeneticCell.inflowNodes.ALWAYS_ON),
                GeneticCell.nodeAttribute.ACTIVATION_VALUE
        ));
        if (TF < 1) {
            throw new Exception("alwaysOn.TF should be > 1, found:" + TF);
        }

        // several more ticks later
        testCell.dgrn.tick();
        testCell.dgrn.tick();
        testCell.dgrn.tick();
        // check activation value
        TF = Integer.parseInt(DGRN.getNodeAttributeValue(
                testCell.dgrn.getNode(GeneticCell.inflowNodes.ALWAYS_ON),
                GeneticCell.nodeAttribute.ACTIVATION_VALUE
        ));
        if (TF < 1) {
            throw new Exception("alwaysOn.TF should be > 1, found:" + TF);
        }
    }

    @Test
    public void testColorizeAfterEnoughTicks() throws Exception {
        GeneticCell testCell = new GeneticCell(0, new TestCell1());
        // colorAdd should achieve activation level of 2 after a few ticks
        //       and color should therefore be lighter
        Color color_0 = new Color(testCell.getColor());
        testCell.dgrn.tick();
        testCell.dgrn.tick();
        testCell.dgrn.tick();
        testCell.dgrn.tick();
        Color color_f = testCell.getColor();

        logger.debug("0:" + color_0 + " f: " + color_f);

        assert (color_0.r < color_f.r);
        assert (color_0.g < color_f.g);
        assert (color_0.b < color_f.b);
    }

//    @Test
//    public void testColorReachesSteadyState() throws Exception{
//        GeneticCell testCell = getMockGeneticCell_1();
//        // colorAdd should achieve activation level of 2 after 2 ticks
//        testCell.dgrn.tick();
//        testCell.dgrn.tick();
//        Color color_0 = new Color(testCell.getColor());
//        // additional ticks should not matter, should now be steady
//        testCell.dgrn.tick();
//        testCell.dgrn.tick();
//        testCell.dgrn.tick();
//        testCell.dgrn.tick();
//        testCell.dgrn.tick();
//        Color color_f = testCell.getColor();
//
//        Assert.assertEquals(color_0.r, color_f.r);
//        Assert.assertEquals(color_0.g, color_f.g);
//        Assert.assertEquals(color_0.b, color_f.b);
//    }

//    @Test
//    public void testAddBlueInMockNet(){
//        GeneticCell testCell = new GeneticCell(1);
//        GeneticCell.buildSeedNetwork(testCell.dgrn);
//        Color color_0 = new Color(testCell.getColor());
//        testCell.dgrn.tick();
//        testCell.dgrn.tick();
//        testCell.dgrn.tick();
//        testCell.dgrn.tick();
//        testCell.dgrn.tick();
//        Color color_f = testCell.getColor();
//        Assert.assertEquals(color_0.r, color_f.r);
//        Assert.assertEquals(color_0.g, color_f.g);
//
//        assert(color_0.b < color_f.b);
//    }
}
