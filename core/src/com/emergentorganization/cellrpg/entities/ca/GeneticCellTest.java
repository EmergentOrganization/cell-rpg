package com.emergentorganization.cellrpg.entities.ca;

import com.badlogic.gdx.graphics.Color;
import com.emergentorganization.cellrpg.entities.ca.DGRN4j.DGRN;
import com.emergentorganization.cellrpg.entities.ca.DGRN4j.GraphInitializer;
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
        testCell.dgrn.graph.createNode(TEST_INNER_NODE_ID_1);
        testCell.dgrn.connect(GeneticCell.inflowNodes.ALWAYS_ON, TEST_INNER_NODE_ID_1, 1);
        testCell.dgrn.connect(TEST_INNER_NODE_ID_1, GeneticCell.outflowNodes.COLOR_LIGHTEN, 2);
        return testCell;
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
    public void testColorizeAfterEnoughTicks() throws Exception{
        GeneticCell testCell = getMockGeneticCell_1();
        // colorAdd should achieve activation level of 2 after 2 ticks
        //       and color should therefore be lighter
        Color color_0 = testCell.getColor();
        testCell.dgrn.tick();
        testCell.dgrn.tick();
        Color color_f = testCell.getColor();

        assert (color_0.r <= color_f.r);
        assert (color_0.g <= color_f.g);
        assert (color_0.b <= color_f.b);
    }

    @Test
    public void testColorReachesSteadyState() throws Exception{
        GeneticCell testCell = getMockGeneticCell_1();
        // colorAdd should achieve activation level of 2 after 2 ticks
        testCell.dgrn.tick();
        testCell.dgrn.tick();
        Color color_0 = testCell.getColor();
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
}
