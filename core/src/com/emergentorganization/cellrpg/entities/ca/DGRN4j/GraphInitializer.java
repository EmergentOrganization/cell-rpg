package com.emergentorganization.cellrpg.entities.ca.DGRN4j;

import com.emergentorganization.cellrpg.entities.ca.GeneticCell;
import it.uniroma1.dis.wsngroup.gexf4j.core.Graph;
import it.uniroma1.dis.wsngroup.gexf4j.core.Node;

/**
 * Created by 7yl4r on 10/4/2015.
 */
public class GraphInitializer {
    public static void buildLightenCellTestGraph(Graph graph) {
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
}
