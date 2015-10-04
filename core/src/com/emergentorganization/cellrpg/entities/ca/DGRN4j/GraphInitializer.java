package com.emergentorganization.cellrpg.entities.ca.DGRN4j;

import com.emergentorganization.cellrpg.entities.ca.GeneticCell;
import it.uniroma1.dis.wsngroup.gexf4j.core.Graph;
import it.uniroma1.dis.wsngroup.gexf4j.core.Node;

/**
 * Created by 7yl4r on 10/4/2015.
 */
public class GraphInitializer {
    public static class inflowNode{
        public static String ALWAYS_ON = "always_on";
    }
    public static class innerNode{
        public static String TF1 = "tf-1";
    }
    public static class outflowNode{
        public static String COLOR_LIGHTEN = "Lighten Color";
    }
    public static void buildLightenCellTestGraph(DGRN dgrn) {
        // Create test graph of shape:
        //   (on) -> (TF1) -> (colorAdd)
        //  (onClick) -^

        Node alwaysOn = dgrn.graph.createNode(inflowNode.ALWAYS_ON);
        alwaysOn
                .setLabel("always on")
                .getAttributeValues()
                .addValue(dgrn.attr_ActivationValue, "1");

        Node TF1 = dgrn.graph.createNode(innerNode.TF1);
        TF1
                .setLabel("TF1")
                .getAttributeValues()
                .addValue(dgrn.attr_ActivationValue, "0");

        Node colorAdd1 = dgrn.graph.createNode(outflowNode.COLOR_LIGHTEN);
        colorAdd1
                .setLabel("colorAdd(x,x,x)")
                .getAttributeValues()
                .addValue(dgrn.attr_ActivationValue, "0");


        alwaysOn.connectTo("0", TF1).setWeight(1);
        TF1.connectTo("1", colorAdd1).setWeight(2);
    }
}
