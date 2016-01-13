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


public class InheritanceTest {
    private final Logger logger = LogManager.getLogger(getClass());

    private DGRN getMockParent1() throws KeySelectorException {
        AttributeList attrList = new AttributeListImpl(AttributeClass.NODE);
        Attribute attr_ActivationValue = attrList.createAttribute(
                "activation value id",
                AttributeType.INTEGER,
                "activation value"
        ).setDefaultValue("0");

        MockInflowNodeHandler inflowHandle = new MockInflowNodeHandler();
        inflowHandle.setInflowNodeList(new String[]{GraphInitializer.inflowNode.ALWAYS_ON});

        return new DGRN(
                "InheritanceTest.java",
                "Test Digital Gene Regulatory Network",
                attrList,
                attr_ActivationValue,
                new MockOutflowNodeHandler(),
                inflowHandle
        );
    }

    @Test
    public void testTwoIdenticalParentsYieldClone() throws KeySelectorException {
        DGRN parent1 = getMockParent1();
        GraphInitializer.buildDGRNExample_homozygous(parent1);
        DGRN parent2 = getMockParent1();
        GraphInitializer.buildDGRNExample_homozygous(parent2);

        DGRN child = getMockParent1();
        child.inheritGenes(parent1, 2);
        child.inheritGenes(parent2, 2);

        // check same # of nodes
        List<Node> pNodes = parent1.graph.getNodes();
        List<Node> cNodes = child.graph.getNodes();
        Assert.assertEquals(pNodes.size(), cNodes.size());

        // check same # of edges
        List<Edge> pEdges = parent1.graph.getAllEdges();
        List<Edge> cEdges = child.graph.getAllEdges();
        Assert.assertEquals(pEdges.size(), cEdges.size());
    }
}
