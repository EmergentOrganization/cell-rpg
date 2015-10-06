package com.emergentorganization.cellrpg.entities.ca.DGRN4j;

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

/**
 * Created by 7yl4r on 10/3/2015.
 */
public class InheritanceTest {
    private final Logger logger = LogManager.getLogger(getClass());

    private DGRN getMockParent1() throws KeySelectorException{
        AttributeList attrList = new AttributeListImpl(AttributeClass.NODE);
        Attribute attr_ActivationValue = attrList.createAttribute(
                "activation value id",
                AttributeType.INTEGER,
                "activation value"
        ).setDefaultValue("0");

        MockInflowNodeHandler inflowHandle =  new MockInflowNodeHandler();
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
    public void testTwoIdenticalParentsYieldClone() throws KeySelectorException{
        DGRN parent1 = getMockParent1();
        GraphInitializer.buildDGRNExample_homozygous(parent1);
        DGRN parent2 = getMockParent1();
        GraphInitializer.buildDGRNExample_homozygous(parent2);

        DGRN child = getMockParent1();
        child.inheritGenes(parent1, 2);
        child.inheritGenes(parent2, 2);

        // TODO: finish this test

       return;
    }
}
