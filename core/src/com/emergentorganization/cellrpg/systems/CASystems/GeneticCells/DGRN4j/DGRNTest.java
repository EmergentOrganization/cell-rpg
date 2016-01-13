package com.emergentorganization.cellrpg.systems.CASystems.GeneticCells.DGRN4j;

import it.uniroma1.dis.wsngroup.gexf4j.core.Edge;
import it.uniroma1.dis.wsngroup.gexf4j.core.Node;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.Attribute;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeClass;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeList;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.data.AttributeListImpl;
import junit.framework.Assert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import javax.xml.crypto.KeySelectorException;
import java.util.List;


public class DGRNTest {
    private final Logger logger = LogManager.getLogger(getClass());

    public DGRN getMockDGRN(){
        AttributeList attrList = new AttributeListImpl(AttributeClass.NODE);
        Attribute attr_ActivationValue = attrList.createAttribute(
                "activation value id",
                AttributeType.INTEGER,
                "activation value"
        ).setDefaultValue("0");

        MockInflowNodeHandler inflowHandle =  new MockInflowNodeHandler();
        inflowHandle.setInflowNodeList(new String[]{GraphInitializer.inflowNode.ALWAYS_ON});

        return new DGRN(
                "DGRNTest.java",
                "Test Digital Gene Regulatory Network",
                attrList,
                attr_ActivationValue,
                new MockOutflowNodeHandler(),
                inflowHandle
        );
    }

    public DGRN getMockDGRN_twoNode() throws Exception{
        AttributeList attrList = new AttributeListImpl(AttributeClass.NODE);
        Attribute attr_ActivationValue = attrList.createAttribute(
                "activation value id",
                AttributeType.INTEGER,
                "activation value"
        ).setDefaultValue("0");


        MockInflowNodeHandler inflowHandle =  new MockInflowNodeHandler();
        inflowHandle.setInflowNodeList(new String[]{GraphInitializer.inflowNode.ALWAYS_ON});
        DGRN dgrn = new DGRN(
                "DGRNTest.java",
                "Test Digital Gene Regulatory Network",
                attrList,
                attr_ActivationValue,
                new MockOutflowNodeHandler(),
               inflowHandle
        );

        Node TF1 = dgrn.graph.createNode(GraphInitializer.innerNode.TF1);
        TF1
                .setLabel(GraphInitializer.innerNode.TF1)
                .getAttributeValues()
                .addValue(dgrn.attr_ActivationValue, "0");

        dgrn.connect(GraphInitializer.inflowNode.ALWAYS_ON, GraphInitializer.innerNode.TF1, 2);

        return dgrn;
    }

    @Test
    public void testGetNode() throws KeySelectorException{
        DGRN dgrn = getMockDGRN();
        GraphInitializer.buildLightenCellTestGraph(dgrn);
        Assert.assertEquals(
                dgrn.getNode(GraphInitializer.inflowNode.ALWAYS_ON).getId(),
                GraphInitializer.inflowNode.ALWAYS_ON
        );
        Assert.assertEquals(dgrn.getNode(GraphInitializer.innerNode.TF1).getId(), GraphInitializer.innerNode.TF1);
        Assert.assertEquals(
                dgrn.getNode(GraphInitializer.outflowNode.COLOR_LIGHTEN).getId(),
                GraphInitializer.outflowNode.COLOR_LIGHTEN
        );
    }

    @Test
    public void testSignalShouldNotPropagateEdgeIfNotEnoughSrcNodePotential() throws Exception{
        // tests when signal should propagate
        DGRN dgrn = getMockDGRN_twoNode();
        List<Edge> edges = dgrn.graph.getAllEdges();
        Assert.assertEquals(edges.size(), 1);  // only 1 edge in this graph
        Edge edge = edges.get(0);
        // init state:
        // (1) -2-> (0)
        Assert.assertEquals(dgrn.edgePropagatesSignal(edge), false);
    }

    @Test
    public void testSignalShouldPropagateEdgeIfMoreThanEnoughSrcNodePotential() throws Exception {
        // tests when signal should propagate
        DGRN dgrn = getMockDGRN_twoNode();
        List<Edge> edges = dgrn.graph.getAllEdges();
        Assert.assertEquals(edges.size(), 1);  // only 1 edge in this graph
        Edge edge = edges.get(0);
        // init state:
        // (1) -2-> (0)
        // set new state:
        // (3) -2-> (0)
        dgrn.setNodeAttributeValue(edge.getSource(), dgrn.ACTIVATION_VALUE_ID, "3");
        Assert.assertEquals(true, dgrn.edgePropagatesSignal(edge));
    }

    @Test
    public void testSignalShouldNotPropagateEdgeIfLargeNegativePotential() throws Exception {
        // tests when signal should propagate
        DGRN dgrn = getMockDGRN_twoNode();
        List<Edge> edges = dgrn.graph.getAllEdges();
        Assert.assertEquals(edges.size(), 1);  // only 1 edge in this graph
        Edge edge = edges.get(0);
        // init state:
        // (1) -2-> (0)
        // set new state:
        // (-5) -2-> (0)
        dgrn.setNodeAttributeValue(dgrn.getNode(GraphInitializer.inflowNode.ALWAYS_ON), dgrn.ACTIVATION_VALUE_ID, "-5");
        Assert.assertEquals(dgrn.edgePropagatesSignal(edge), false);
    }

    @Test
    public void testSignalShouldPropagateEdgeIfExactlyEnoughSrcNodePotential() throws Exception {
        // tests when signal should propagate
        DGRN dgrn = getMockDGRN_twoNode();
        List<Edge> edges = dgrn.graph.getAllEdges();
        Assert.assertEquals(edges.size(), 1);  // only 1 edge in this graph
        Edge edge = edges.get(0);
        // init state:
        // (1) -2-> (0)
        // set new state:
        // (2) -2-> (0)

        logger.debug(edge.getSource().getId() + "->" + edge.getTarget().getId());

        // TODO: this doesn't work:
        dgrn.setNodeAttributeValue(dgrn.getNode(GraphInitializer.inflowNode.ALWAYS_ON), dgrn.ACTIVATION_VALUE_ID, "2");
        // TODO: this does work:
        dgrn.setNodeAttributeValue(edge.getSource(), dgrn.ACTIVATION_VALUE_ID, "2");
        // TODO: WHY?

        String val = dgrn.getNodeAttributeValue(dgrn.getNode(GraphInitializer.inflowNode.ALWAYS_ON), dgrn.ACTIVATION_VALUE_ID);
        String val2= dgrn.getNodeAttributeValue(edge.getSource(), dgrn.ACTIVATION_VALUE_ID);
        logger.debug("srcNode.Id:" + edge.getSource().getId());
        Assert.assertEquals(val, val2);  // check that src node is what we think it is
        logger.debug("src node value:" + val);
        Assert.assertEquals(true, dgrn.edgePropagatesSignal(edge));
    }

    @Test
    public void testPrimeInflowNodesSetsAlwaysOnTo1() throws Exception{
        DGRN dgrn = getMockDGRN_twoNode();
        List<Edge> edges = dgrn.graph.getAllEdges();
        Assert.assertEquals(edges.size(), 1);  // only 1 edge in this graph
        Edge edge = edges.get(0);
        // init state:
        // (1) -2-> (0)
        // set new state:
        // (-5) -2-> (0)
        dgrn.setNodeAttributeValue(dgrn.getNode(GraphInitializer.inflowNode.ALWAYS_ON), dgrn.ACTIVATION_VALUE_ID, "-5");
        // re-prime should set to :
        // (1) -2-> (0)
        dgrn.primeInflowNodes();
        Assert.assertEquals(
                dgrn.getNodeAttributeValue(
                    dgrn.getNode(GraphInitializer.inflowNode.ALWAYS_ON),
                    dgrn.ACTIVATION_VALUE_ID),
                "1"
        );
    }

    @Test
    public void testTicksPropagateStrengthForActiveNodes() throws Exception {
        logger.info("running DGRN signal propagation test...");
        DGRN dgrn = getMockDGRN();
        GraphInitializer.buildLightenCellTestGraph(dgrn);
        // assuming default structure:
        //   (on) -a-> (TF1) -b-> (colorAdd)
        // where weights of a=1 and b=2

        String attr = dgrn.attr_ActivationValue.getId();
        // before tick values should be
        //   (1) -> (0) -> (0)
        Assert.assertEquals("1", DGRN.getNodeAttributeValue(
                        dgrn.getNode(GraphInitializer.inflowNode.ALWAYS_ON),
                        attr)
        );
        Assert.assertEquals("0", DGRN.getNodeAttributeValue(dgrn.getNode(GraphInitializer.innerNode.TF1), attr));
        Assert.assertEquals(
                "0",
                DGRN.getNodeAttributeValue(
                        dgrn.getNode(GraphInitializer.outflowNode.COLOR_LIGHTEN),
                        attr
                )
        );

        dgrn.tick();
        // after 1 tick
        //   (1) -> (1) -> (0)
        String alwaysOnV = DGRN.getNodeAttributeValue(dgrn.getNode(GraphInitializer.inflowNode.ALWAYS_ON), attr);
        String TF1V = DGRN.getNodeAttributeValue(dgrn.getNode(GraphInitializer.innerNode.TF1), attr);
        String lightenV = DGRN.getNodeAttributeValue(dgrn.getNode(GraphInitializer.outflowNode.COLOR_LIGHTEN), attr);
        logger.debug("t=1 : " + "(" + alwaysOnV + ")->(" + TF1V + ")->(" + lightenV + ")");
        Assert.assertEquals("1", alwaysOnV);
        Assert.assertEquals("1", TF1V);
        Assert.assertEquals("0", lightenV);

        dgrn.tick();
        alwaysOnV = DGRN.getNodeAttributeValue(dgrn.getNode(GraphInitializer.inflowNode.ALWAYS_ON), attr);
        TF1V = DGRN.getNodeAttributeValue(dgrn.getNode(GraphInitializer.innerNode.TF1), attr);
        lightenV = DGRN.getNodeAttributeValue(dgrn.getNode(GraphInitializer.outflowNode.COLOR_LIGHTEN), attr);
        logger.debug("t=2 : " + "(" + alwaysOnV + ")->(" + TF1V + ")->(" + lightenV + ")");
        // after 2 ticks
        //   (1) -> (2) -> (0)
        Assert.assertEquals("1", alwaysOnV);
        Assert.assertEquals("2", TF1V);
        Assert.assertEquals("0",lightenV);

        // 3 ticks
        //  (1) -> (1) -> (2)
        dgrn.tick();
        Assert.assertEquals("1", DGRN.getNodeAttributeValue(
                        dgrn.getNode(GraphInitializer.inflowNode.ALWAYS_ON),
                        attr)
        );
        Assert.assertEquals("1", DGRN.getNodeAttributeValue(dgrn.getNode(GraphInitializer.innerNode.TF1), attr));
        Assert.assertEquals(
                "2",
                DGRN.getNodeAttributeValue(
                        dgrn.getNode(GraphInitializer.outflowNode.COLOR_LIGHTEN),
                        attr
                )
        );

        // 4 ticks
        //  (1) -> (2) -> (2)
        dgrn.tick();
        Assert.assertEquals("1", DGRN.getNodeAttributeValue(
                        dgrn.getNode(GraphInitializer.inflowNode.ALWAYS_ON),
                        attr)
        );
        Assert.assertEquals("2", DGRN.getNodeAttributeValue(dgrn.getNode(GraphInitializer.innerNode.TF1), attr));
        Assert.assertEquals(
                "2",
                DGRN.getNodeAttributeValue(
                        dgrn.getNode(GraphInitializer.outflowNode.COLOR_LIGHTEN),
                        attr
                )
        );

        // 5 ticks
        //  (1) -> (1) -> (4)
        dgrn.tick();
        Assert.assertEquals("1", DGRN.getNodeAttributeValue(
                        dgrn.getNode(GraphInitializer.inflowNode.ALWAYS_ON),
                        attr)
        );
        Assert.assertEquals("1", DGRN.getNodeAttributeValue(dgrn.getNode(GraphInitializer.innerNode.TF1), attr));
        Assert.assertEquals(
                "4",
                DGRN.getNodeAttributeValue(
                        dgrn.getNode(GraphInitializer.outflowNode.COLOR_LIGHTEN),
                        attr
                )
        );
    }

    @Test
    public void testGetAllEdgesOf() {
        // TODO: compare node.getAllEdges() and node.getEdges()
        // TODO: do either of the above include inflowing edges AND outflowing edges?
        DGRN dgrn = getMockDGRN();

        Node in = dgrn.graph.createNode("in");
        Node middle = dgrn.graph.createNode("middle");
        Node out = dgrn.graph.createNode("out");

        in.connectTo(middle);
        middle.connectTo(out);

        List<Edge> edges = dgrn.getAllEdgesOf(middle);
        Assert.assertEquals(2, edges.size());
    }
}
