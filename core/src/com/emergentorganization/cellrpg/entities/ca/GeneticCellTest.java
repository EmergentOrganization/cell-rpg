package com.emergentorganization.cellrpg.entities.ca;

import com.badlogic.gdx.graphics.Color;
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
        testCell.getNode(GeneticCell.inflowNodes.ALWAYS_ON.toString());
    }

    @Test
    public void testDefaultGraphAlwaysOnNodeIsActive() throws Exception {
        GeneticCell testCell = new GeneticCell(0);
        testCell.setGraphToDefault();
        int TF = Integer.parseInt(GeneticCell.getNodeAttributeValue(
                testCell.getNode(GeneticCell.inflowNodes.ALWAYS_ON.toString()),
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
        Assert.assertEquals(
                testCell.getNode(GeneticCell.inflowNodes.ALWAYS_ON.toString()).getId(),
                GeneticCell.inflowNodes.ALWAYS_ON.toString()
        );
        Assert.assertEquals(testCell.getNode("TF1").getId(), "TF1");
        Assert.assertEquals(
                testCell.getNode(GeneticCell.outflowNodes.COLOR_LIGHTEN.toString()).getId(),
                GeneticCell.outflowNodes.COLOR_LIGHTEN.toString()
        );
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
        Assert.assertEquals("1", GeneticCell.getNodeAttributeValue(
                testCell.getNode(GeneticCell.inflowNodes.ALWAYS_ON.toString()),
                attr)
        );
        Assert.assertEquals("0", GeneticCell.getNodeAttributeValue(testCell.getNode("TF1"), attr));
        Assert.assertEquals(
                "0",
                GeneticCell.getNodeAttributeValue(
                        testCell.getNode(GeneticCell.outflowNodes.COLOR_LIGHTEN.toString()),
                        attr
                )
        );

        testCell.tick();
        // after 1 tick
        //   (1) -> (1) -> (0)
        Assert.assertEquals("1", GeneticCell.getNodeAttributeValue(
                        testCell.getNode(GeneticCell.inflowNodes.ALWAYS_ON.toString()),
                        attr)
        );
        Assert.assertEquals("1", GeneticCell.getNodeAttributeValue(testCell.getNode("TF1"), attr));
        Assert.assertEquals(
                "0",
                GeneticCell.getNodeAttributeValue(
                        testCell.getNode(GeneticCell.outflowNodes.COLOR_LIGHTEN.toString()),
                        attr
                )
        );

        testCell.tick();
        // after 2 ticks
        //   (1) -> (1) -> (2)
        Assert.assertEquals("1", GeneticCell.getNodeAttributeValue(
                        testCell.getNode(GeneticCell.inflowNodes.ALWAYS_ON.toString()),
                        attr)
        );
        Assert.assertEquals("1", GeneticCell.getNodeAttributeValue(testCell.getNode("TF1"), attr));
        Assert.assertEquals(
                "2",
                GeneticCell.getNodeAttributeValue(
                        testCell.getNode(GeneticCell.outflowNodes.COLOR_LIGHTEN.toString()),
                        attr
                )
        );

        // now at steady state, extra ticks should make no difference
        testCell.tick();
        testCell.tick();
        testCell.tick();
        Assert.assertEquals("1", GeneticCell.getNodeAttributeValue(
                        testCell.getNode(GeneticCell.inflowNodes.ALWAYS_ON.toString()),
                        attr)
        );
        Assert.assertEquals("1", GeneticCell.getNodeAttributeValue(testCell.getNode("TF1"), attr));
        Assert.assertEquals(
                "2",
                GeneticCell.getNodeAttributeValue(
                        testCell.getNode(GeneticCell.outflowNodes.COLOR_LIGHTEN.toString()),
                        attr
                )
        );

        // NOTE: alternatively, for a "cumulative" network (no steady state):
        // (1) -> (0) -> (0)
        // (1) -> (1) -> (0)
        // (1) -> (2) -> (2)
        // (1) -> (3) -> (4)
    }

    @Test
    public void testColorizeAfterEnoughTicks() throws KeySelectorException{
        GeneticCell testCell = new GeneticCell(0);
        testCell.setGraphToDefault();
        // assuming default structure:
        //   (on) -a-> (TF1) -b-> (colorAdd)
        // where weights of a=1 and b=2
        // thus, colorAdd should achieve activation level of 2 after 2 ticks
        //       and color should therefore be lighter
        Color color_0 = testCell.getColor();
        testCell.tick();
        testCell.tick();
        Color color_f = testCell.getColor();

        assert (color_0.r <= color_f.r);
        assert (color_0.g <= color_f.g);
        assert (color_0.b <= color_f.b);
    }
}
