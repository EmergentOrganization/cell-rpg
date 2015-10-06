package com.emergentorganization.cellrpg.entities.ca;

import com.badlogic.gdx.graphics.Color;
import com.emergentorganization.cellrpg.entities.ca.DGRN4j.DGRN;
import com.emergentorganization.cellrpg.entities.ca.DGRN4j.GraphInitializer;
import it.uniroma1.dis.wsngroup.gexf4j.core.Node;
import junit.framework.Assert;
import org.junit.Test;

import javax.xml.crypto.KeySelectorException;

/**
 * Created by 7yl4r on 10/3/2015.
 */
public class GeneticCellTest {

    private final String TEST_INNER_NODE_ID_1 = "test inner node 1";
    private GeneticCell getMockGeneticCell_1() throws Exception{
        // returns genetic cell with following structure:
        //   (on) -a-> (TF1) -b-> (colorAdd)
        // where weights of a=1 and b=2
        GeneticCell testCell = new GeneticCell(0);
        Node testNode = testCell.dgrn.graph.createNode(TEST_INNER_NODE_ID_1);
        testNode
                .setLabel(TEST_INNER_NODE_ID_1)
                .getAttributeValues()
                .addValue(DGRN.attr_ActivationValue, "0")
                .addValue(DGRN.attr_AlleleCount, "1");
        testCell.dgrn.connect(GeneticCell.inflowNodes.ALWAYS_ON, TEST_INNER_NODE_ID_1, 1);
        testCell.dgrn.connect(TEST_INNER_NODE_ID_1, GeneticCell.outflowNodes.COLOR_LIGHTEN, 2);
        return testCell;
    }

    private GeneticCell getMockGeneticCell_2() throws Exception{
        // same as mockCell 1, but with state=1
        GeneticCell testCell = new GeneticCell(1);
        Node testNode = testCell.dgrn.graph.createNode(TEST_INNER_NODE_ID_1);
        testNode
                .setLabel(TEST_INNER_NODE_ID_1)
                .getAttributeValues()
                .addValue(DGRN.attr_ActivationValue, "0")
                .addValue(DGRN.attr_AlleleCount, "1");
        testCell.dgrn.connect(GeneticCell.inflowNodes.ALWAYS_ON, TEST_INNER_NODE_ID_1, 1);
        testCell.dgrn.connect(TEST_INNER_NODE_ID_1, GeneticCell.outflowNodes.COLOR_LIGHTEN, 2);
        return testCell;
    }

    @Test
    public void testActivationPropagation() throws Exception{
        GeneticCell testCell = getMockGeneticCell_2();
        // t=0, states: 1 -> 0 -> 0
        int TF = Integer.parseInt(DGRN.getNodeAttributeValue(
                testCell.dgrn.getNode(GeneticCell.inflowNodes.ALWAYS_ON),
                GeneticCell.nodeAttribute.ACTIVATION_VALUE
        ));
        assert TF == 1;
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
        assert TF == 1;

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
        // t=0, states: 1 -> 2 -> 0
        TF = Integer.parseInt(DGRN.getNodeAttributeValue(
                testCell.dgrn.getNode(GeneticCell.inflowNodes.ALWAYS_ON),
                GeneticCell.nodeAttribute.ACTIVATION_VALUE
        ));
        assert TF == 1;

        TF = Integer.parseInt(DGRN.getNodeAttributeValue(
                testCell.dgrn.getNode(TEST_INNER_NODE_ID_1),
                GeneticCell.nodeAttribute.ACTIVATION_VALUE
        ));
        Assert.assertEquals(TF, 2);

        TF = Integer.parseInt(DGRN.getNodeAttributeValue(
                testCell.dgrn.getNode(GeneticCell.outflowNodes.COLOR_LIGHTEN),
                GeneticCell.nodeAttribute.ACTIVATION_VALUE
        ));
        Assert.assertEquals(TF, 2);
    }

    @Test
    public void testCellStateIsSetByConstructor() {
        int INIT_STATE = 0;
        GeneticCell testCell = new GeneticCell(INIT_STATE);
        Assert.assertEquals("initial state should be set to " + INIT_STATE, INIT_STATE, testCell.getState());
    }

    @Test
    public void testDefaultGraphHasAlwaysOnNode() throws KeySelectorException {
        GeneticCell testCell = new GeneticCell(0);
        testCell.dgrn.getNode(GeneticCell.inflowNodes.ALWAYS_ON);
    }

    @Test
    public void testDefaultGraphAlwaysOnNodeIsActive() throws Exception {
        GeneticCell testCell = new GeneticCell(0);
        GraphInitializer.buildLightenCellTestGraph(testCell.dgrn);
        int TF = Integer.parseInt(DGRN.getNodeAttributeValue(
                testCell.dgrn.getNode(GeneticCell.inflowNodes.ALWAYS_ON),
                GeneticCell.nodeAttribute.ACTIVATION_VALUE
        ));
        if (TF < 1){
            throw new Exception("alwaysOn.TF should be > 1, found:" + TF);
        }
    }

    @Test
    public void testAlwaysOnStaysActive() throws Exception{
        GeneticCell testCell = new GeneticCell(1);
        GeneticCell.buildMockNetwork(testCell.dgrn);
        // check activation value
        int TF = Integer.parseInt(DGRN.getNodeAttributeValue(
                testCell.dgrn.getNode(GeneticCell.inflowNodes.ALWAYS_ON),
                GeneticCell.nodeAttribute.ACTIVATION_VALUE
        ));
        if (TF < 1){
            throw new Exception("alwaysOn.TF should be > 1, found:" + TF);
        }

        // one tick later
        testCell.dgrn.tick();
        // check activation value
        TF = Integer.parseInt(DGRN.getNodeAttributeValue(
                testCell.dgrn.getNode(GeneticCell.inflowNodes.ALWAYS_ON),
                GeneticCell.nodeAttribute.ACTIVATION_VALUE
        ));
        if (TF < 1){
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
        if (TF < 1){
            throw new Exception("alwaysOn.TF should be > 1, found:" + TF);
        }
    }

    @Test
    public void testColorizeAfterEnoughTicks() throws Exception{
        GeneticCell testCell = getMockGeneticCell_1();
        // colorAdd should achieve activation level of 2 after 2 ticks
        //       and color should therefore be lighter
        Color color_0 = new Color(testCell.getColor());
        testCell.dgrn.tick();
        testCell.dgrn.tick();
        Color color_f = testCell.getColor();

        System.out.println("0:" + color_0 + " f: "+ color_f);

        assert (color_0.r < color_f.r);
        assert (color_0.g < color_f.g);
        assert (color_0.b < color_f.b);
    }

    @Test
    public void testColorReachesSteadyState() throws Exception{
        GeneticCell testCell = getMockGeneticCell_1();
        // colorAdd should achieve activation level of 2 after 2 ticks
        testCell.dgrn.tick();
        testCell.dgrn.tick();
        Color color_0 = new Color(testCell.getColor());
        // additional ticks should not matter, should now be steady
        testCell.dgrn.tick();
        testCell.dgrn.tick();
        testCell.dgrn.tick();
        testCell.dgrn.tick();
        testCell.dgrn.tick();
        Color color_f = testCell.getColor();

        Assert.assertEquals(color_0.r, color_f.r);
        Assert.assertEquals(color_0.g, color_f.g);
        Assert.assertEquals(color_0.b, color_f.b);
    }

    @Test
    public void testAddBlueInMockNet(){
        GeneticCell testCell = new GeneticCell(1);
        GeneticCell.buildMockNetwork(testCell.dgrn);
        Color color_0 = new Color(testCell.getColor());
        testCell.dgrn.tick();
        testCell.dgrn.tick();
        testCell.dgrn.tick();
        testCell.dgrn.tick();
        testCell.dgrn.tick();
        Color color_f = testCell.getColor();
        Assert.assertEquals(color_0.r, color_f.r);
        Assert.assertEquals(color_0.g, color_f.g);

        assert(color_0.b < color_f.b);
    }
}
