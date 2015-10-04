package com.emergentorganization.cellrpg.entities.ca.DGRN4j;

import it.uniroma1.dis.wsngroup.gexf4j.core.Graph;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.Attribute;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeClass;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeList;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.data.AttributeListImpl;
import junit.framework.Assert;
import org.junit.Test;

import javax.xml.crypto.KeySelectorException;

/**
 * Created by 7yl4r on 10/3/2015.
 */
public class DGRNTest {
    public DGRN getMockDGRN(){
        AttributeList attrList = new AttributeListImpl(AttributeClass.NODE);
        Attribute attr_ActivationValue = attrList.createAttribute(
                "activation value id",
                AttributeType.INTEGER,
                "activation value"
        ).setDefaultValue("0");

        return new DGRN(
                "DGRNTest.java",
                "Test Digital Gene Regulatory Network",
                attrList,
                attr_ActivationValue,
                new MockOutflowNodeHandler(),
                new MockInflowNodeHandler()
        );
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
    public void testTicksPropagateStrengthForActiveNodes() throws Exception{
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
        Assert.assertEquals("1", DGRN.getNodeAttributeValue(
                        dgrn.getNode(GraphInitializer.inflowNode.ALWAYS_ON),
                        attr)
        );
        Assert.assertEquals("1", DGRN.getNodeAttributeValue(dgrn.getNode(GraphInitializer.innerNode.TF1), attr));
        Assert.assertEquals(
                "0",
                DGRN.getNodeAttributeValue(
                        dgrn.getNode(GraphInitializer.outflowNode.COLOR_LIGHTEN),
                        attr
                )
        );

        dgrn.tick();
        // after 2 ticks
        //   (1) -> (1) -> (2)
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

        // now at steady state, extra ticks should make no difference
        dgrn.tick();
        dgrn.tick();
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

        // NOTE: alternatively, for a "cumulative" network (no steady state):
        // (1) -> (0) -> (0)
        // (1) -> (1) -> (0)
        // (1) -> (2) -> (2)
        // (1) -> (3) -> (4)
    }
}
