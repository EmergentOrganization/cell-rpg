package io.github.emergentorganization.cellrpg.systems.CASystems.GeneticCells.DGRN4j;

import it.uniroma1.dis.wsngroup.gexf4j.core.Node;

import javax.xml.crypto.KeySelectorException;


public class GraphInitializer {
    public static void buildLightenCellTestGraph(DGRN dgrn) throws KeySelectorException {
        // Create test graph of shape:
        //   (on) -> (TF1) -> (colorAdd)
        //  (onClick) -^

        Node alwaysOn = dgrn.graph.createNode(inflowNode.ALWAYS_ON);
        alwaysOn.setLabel(inflowNode.ALWAYS_ON)
                .getAttributeValues()
                .addValue(DGRN.attr_ActivationValue, "1");

        Node TF1 = dgrn.graph.createNode(innerNode.TF1);
        TF1
                .setLabel("TF1")
                .getAttributeValues()
                .addValue(DGRN.attr_ActivationValue, "0");

        Node colorAdd1 = dgrn.graph.createNode(outflowNode.COLOR_LIGHTEN);
        colorAdd1
                .setLabel("colorAdd(x,x,x)")
                .getAttributeValues()
                .addValue(DGRN.attr_ActivationValue, "0");

        dgrn.connect(inflowNode.ALWAYS_ON, innerNode.TF1, 1);
        TF1.connectTo("1", colorAdd1).setWeight(2);
    }

    public static void buildDGRNExample_homozygous(DGRN dgrn) throws KeySelectorException {
        Node TF1 = dgrn.graph.createNode(innerNode.TF1);
        TF1
                .setLabel("TF1")
                .getAttributeValues()
                .addValue(DGRN.attr_ActivationValue, "0")
                .addValue(DGRN.attr_AlleleCount, "2");

        Node colorAdd1 = dgrn.graph.createNode(outflowNode.COLOR_LIGHTEN);
        colorAdd1
                .setLabel("colorAdd(x,x,x)")
                .getAttributeValues()
                .addValue(DGRN.attr_ActivationValue, "0")
                .addValue(DGRN.attr_AlleleCount, "2");

        dgrn.connect(inflowNode.ALWAYS_ON, innerNode.TF1, 1);
        TF1.connectTo("1", colorAdd1).setWeight(10);
    }

    public static class inflowNode {
        public static final String ALWAYS_ON = "always_on";
    }

    public static class innerNode {
        public static final String TF1 = "tf-1";
    }

    public static class outflowNode {
        public static final String COLOR_LIGHTEN = "Lighten Color";
    }
}
