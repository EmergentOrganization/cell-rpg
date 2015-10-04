package com.emergentorganization.cellrpg.entities.ca;

import junit.framework.Assert;
import org.junit.Test;

import javax.xml.crypto.KeySelectorException;

/**
 * Created by 7yl4r on 10/3/2015.
 */
public class GeneticCellTest {
    /*
    @Test(expected = IllegalArgumentException.class)
    public void testExceptionIsThrown() {
        MyClass tester = new MyClass();
        tester.multiply(1000, 5);
    }
    */

    @Test
    public void testCellStateIsSetByConstructor() {
        int INIT_STATE = 0;
        GeneticCell testCell = new GeneticCell(INIT_STATE);
        Assert.assertEquals("initial state should be set to " + INIT_STATE, INIT_STATE, testCell.getState());
    }

    @Test
    public void testDefaultGraphHasAlwaysOnNode() throws KeySelectorException {
        GeneticCell testCell = new GeneticCell(0);
        testCell.setGraphToDefault();
        testCell.getNode("alwaysOn");
    }

    @Test
    public void testDefaultGraphAlwaysOnNodeIsActive() throws Exception {
        GeneticCell testCell = new GeneticCell(0);
        testCell.setGraphToDefault();
        int TF = Integer.parseInt(GeneticCell.getNodeAttributeValue(
                testCell.getNode("alwaysOn"),
                GeneticCell.nodeAttribute.ACTIVATION_VALUE.toString()
        ));
        if (TF < 1){
            throw new Exception("alwaysOn.TF should be > 1, found:" + TF);
        }
    }

    @Test
    public void testGetNode() throws KeySelectorException{
        GeneticCell testCell = new GeneticCell(0);
        testCell.setGraphToDefault();
        Assert.assertEquals(testCell.getNode("alwaysOn").getId(), "alwaysOn");
        Assert.assertEquals(testCell.getNode("TF1").getId(), "TF1");
        Assert.assertEquals(testCell.getNode("colorAdd(50,50,50)").getId(), "colorAdd(50,50,50)");
    }

    @Test
    public void testTicksPropagateStrengthForActiveNodes() throws KeySelectorException{
        GeneticCell testCell = new GeneticCell(0);
        testCell.setGraphToDefault();
        // assuming default structure:
        //   (on) -a-> (TF1) -b-> (colorAdd)
        // where weights of a=1 and b=2

        GeneticCell.nodeAttribute attr = GeneticCell.nodeAttribute.ACTIVATION_VALUE;
        // before tick values should be
        //   (1) -> (0) -> (0)
        Assert.assertEquals("1", GeneticCell.getNodeAttributeValue(testCell.getNode("alwaysOn"), attr));
        Assert.assertEquals("0", GeneticCell.getNodeAttributeValue(testCell.getNode("TF1"), attr));
        Assert.assertEquals("0", GeneticCell.getNodeAttributeValue(testCell.getNode("colorAdd(50,50,50)"), attr));

        testCell.tick();
        // after 1 tick
        //   (1) -> (1) -> (0)
        Assert.assertEquals("1", GeneticCell.getNodeAttributeValue(testCell.getNode("alwaysOn"), attr));
        Assert.assertEquals("1", GeneticCell.getNodeAttributeValue(testCell.getNode("TF1"), attr));
        Assert.assertEquals("0", GeneticCell.getNodeAttributeValue(testCell.getNode("colorAdd(50,50,50)"), attr));

        testCell.tick();
        // after 2 ticks
        //   (1) -> (1) -> (2)
        Assert.assertEquals("1", GeneticCell.getNodeAttributeValue(testCell.getNode("alwaysOn"), attr));
        Assert.assertEquals("1", GeneticCell.getNodeAttributeValue(testCell.getNode("TF1"), attr));
        Assert.assertEquals("2", GeneticCell.getNodeAttributeValue(testCell.getNode("colorAdd(50,50,50)"), attr));

        // now at steady state, extra ticks should make no difference
        testCell.tick();
        testCell.tick();
        testCell.tick();
        Assert.assertEquals("1", GeneticCell.getNodeAttributeValue(testCell.getNode("alwaysOn"), attr));
        Assert.assertEquals("1", GeneticCell.getNodeAttributeValue(testCell.getNode("TF1"), attr));
        Assert.assertEquals("2", GeneticCell.getNodeAttributeValue(testCell.getNode("colorAdd(50,50,50)"), attr));

        // NOTE: alternatively, for a "cumulative" network (no steady state):
        // (1) -> (0) -> (0)
        // (1) -> (1) -> (0)
        // (1) -> (2) -> (2)
        // (1) -> (3) -> (4)
    }
}
