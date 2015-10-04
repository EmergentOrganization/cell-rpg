package com.emergentorganization.cellrpg.entities.ca;

import com.badlogic.gdx.graphics.Color;
import it.uniroma1.dis.wsngroup.gexf4j.core.Graph;
import it.uniroma1.dis.wsngroup.gexf4j.core.Node;
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
    public void buildLightenCellTestGraph(Graph graph) {
        // Create test graph of shape:
        //   (on) -> (TF1) -> (colorAdd)
        //  (onClick) -^

        Node alwaysOn = graph.createNode(GeneticCell.inflowNodes.ALWAYS_ON);
        alwaysOn
                .setLabel("always on")
                .getAttributeValues()
                .addValue(GeneticCell.attr_ActivationValue, "1");

        Node TF1 = graph.createNode("TF1");
        TF1
                .setLabel("TF1")
                .getAttributeValues()
                .addValue(GeneticCell.attr_ActivationValue, "0");

        Node colorAdd1 = graph.createNode(GeneticCell.outflowNodes.COLOR_LIGHTEN);
        colorAdd1
                .setLabel("colorAdd(x,x,x)")
                .getAttributeValues()
                .addValue(GeneticCell.attr_ActivationValue, "0");


        alwaysOn.connectTo("0", TF1).setWeight(1);
        TF1.connectTo("1", colorAdd1).setWeight(2);
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
        buildLightenCellTestGraph(testCell.graph);
        testCell.getNode(GeneticCell.inflowNodes.ALWAYS_ON);
    }

    @Test
    public void testDefaultGraphAlwaysOnNodeIsActive() throws Exception {
        GeneticCell testCell = new GeneticCell(0);
        buildLightenCellTestGraph(testCell.graph);
        int TF = Integer.parseInt(GeneticCell.getNodeAttributeValue(
                testCell.getNode(GeneticCell.inflowNodes.ALWAYS_ON),
                GeneticCell.nodeAttribute.ACTIVATION_VALUE
        ));
        if (TF < 1){
            throw new Exception("alwaysOn.TF should be > 1, found:" + TF);
        }
    }

    @Test
    public void testGetNode() throws KeySelectorException{
        GeneticCell testCell = new GeneticCell(0);
        buildLightenCellTestGraph(testCell.graph);
        Assert.assertEquals(
                testCell.getNode(GeneticCell.inflowNodes.ALWAYS_ON).getId(),
                GeneticCell.inflowNodes.ALWAYS_ON
        );
        Assert.assertEquals(testCell.getNode("TF1").getId(), "TF1");
        Assert.assertEquals(
                testCell.getNode(GeneticCell.outflowNodes.COLOR_LIGHTEN).getId(),
                GeneticCell.outflowNodes.COLOR_LIGHTEN
        );
    }

    @Test
    public void testTicksPropagateStrengthForActiveNodes() throws KeySelectorException{
        GeneticCell testCell = new GeneticCell(0);
        buildLightenCellTestGraph(testCell.graph);
        // assuming default structure:
        //   (on) -a-> (TF1) -b-> (colorAdd)
        // where weights of a=1 and b=2

        String attr = GeneticCell.nodeAttribute.ACTIVATION_VALUE;
        // before tick values should be
        //   (1) -> (0) -> (0)
        Assert.assertEquals("1", GeneticCell.getNodeAttributeValue(
                testCell.getNode(GeneticCell.inflowNodes.ALWAYS_ON),
                attr)
        );
        Assert.assertEquals("0", GeneticCell.getNodeAttributeValue(testCell.getNode("TF1"), attr));
        Assert.assertEquals(
                "0",
                GeneticCell.getNodeAttributeValue(
                        testCell.getNode(GeneticCell.outflowNodes.COLOR_LIGHTEN),
                        attr
                )
        );

        testCell.tick();
        // after 1 tick
        //   (1) -> (1) -> (0)
        Assert.assertEquals("1", GeneticCell.getNodeAttributeValue(
                        testCell.getNode(GeneticCell.inflowNodes.ALWAYS_ON),
                        attr)
        );
        Assert.assertEquals("1", GeneticCell.getNodeAttributeValue(testCell.getNode("TF1"), attr));
        Assert.assertEquals(
                "0",
                GeneticCell.getNodeAttributeValue(
                        testCell.getNode(GeneticCell.outflowNodes.COLOR_LIGHTEN),
                        attr
                )
        );

        testCell.tick();
        // after 2 ticks
        //   (1) -> (1) -> (2)
        Assert.assertEquals("1", GeneticCell.getNodeAttributeValue(
                        testCell.getNode(GeneticCell.inflowNodes.ALWAYS_ON),
                        attr)
        );
        Assert.assertEquals("1", GeneticCell.getNodeAttributeValue(testCell.getNode("TF1"), attr));
        Assert.assertEquals(
                "2",
                GeneticCell.getNodeAttributeValue(
                        testCell.getNode(GeneticCell.outflowNodes.COLOR_LIGHTEN),
                        attr
                )
        );

        // now at steady state, extra ticks should make no difference
        testCell.tick();
        testCell.tick();
        testCell.tick();
        Assert.assertEquals("1", GeneticCell.getNodeAttributeValue(
                        testCell.getNode(GeneticCell.inflowNodes.ALWAYS_ON),
                        attr)
        );
        Assert.assertEquals("1", GeneticCell.getNodeAttributeValue(testCell.getNode("TF1"), attr));
        Assert.assertEquals(
                "2",
                GeneticCell.getNodeAttributeValue(
                        testCell.getNode(GeneticCell.outflowNodes.COLOR_LIGHTEN),
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
        buildLightenCellTestGraph(testCell.graph);
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
